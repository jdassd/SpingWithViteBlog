package com.springwithviteblog.service;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.Comment;
import com.springwithviteblog.domain.CommentStatus;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.dto.comment.CommentCreateRequest;
import com.springwithviteblog.dto.comment.CommentDto;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.ArticleMapper;
import com.springwithviteblog.mapper.CommentMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
  private final CommentMapper commentMapper;
  private final ArticleMapper articleMapper;
  private final ArticleService articleService;
  private final SiteSettingService siteSettingService;
  private final RateLimitService rateLimitService;
  private final SensitiveWordService sensitiveWordService;

  public CommentService(CommentMapper commentMapper,
                        ArticleMapper articleMapper,
                        ArticleService articleService,
                        SiteSettingService siteSettingService,
                        RateLimitService rateLimitService,
                        SensitiveWordService sensitiveWordService) {
    this.commentMapper = commentMapper;
    this.articleMapper = articleMapper;
    this.articleService = articleService;
    this.siteSettingService = siteSettingService;
    this.rateLimitService = rateLimitService;
    this.sensitiveWordService = sensitiveWordService;
  }

  @Transactional
  public CommentDto create(Long articleId, User user, CommentCreateRequest request) {
    Article article = articleMapper.findById(articleId);
    if (article == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Article not found", HttpStatus.NOT_FOUND);
    }
    if (!articleService.canViewArticle(article, user)) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }

    boolean allowGuest = siteSettingService.getBoolean("allow_guest_comment", false);
    if (user == null && !allowGuest) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Guest comments disabled", HttpStatus.FORBIDDEN);
    }
    if (user == null) {
      if (request.getGuestName() == null || request.getGuestName().isBlank()) {
        throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Guest name required", HttpStatus.BAD_REQUEST);
      }
      if (request.getGuestEmail() == null || request.getGuestEmail().isBlank()) {
        throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Guest email required", HttpStatus.BAD_REQUEST);
      }
    }

    String rateKey = user != null ? "comment:user:" + user.getId() : "comment:guest:" + request.getGuestEmail();
    int limit = Integer.parseInt(siteSettingService.get("comment_rate_limit") == null
        ? "5" : siteSettingService.get("comment_rate_limit"));
    if (!rateLimitService.tryConsume(rateKey, limit, 60)) {
      throw new ApiException(ErrorCodes.OPERATION_NOT_ALLOWED, "Too many comments", HttpStatus.TOO_MANY_REQUESTS);
    }

    CommentStatus status = resolveInitialStatus(user, request);

    List<String> matches = sensitiveWordService.findMatches(request.getContent());
    if (!matches.isEmpty()) {
      String strategy = siteSettingService.get("sensitive_strategy");
      if ("REJECT".equalsIgnoreCase(strategy)) {
        throw new ApiException(ErrorCodes.OPERATION_NOT_ALLOWED, "Content rejected", HttpStatus.BAD_REQUEST);
      }
      status = CommentStatus.PENDING;
    }

    Comment comment = new Comment();
    comment.setArticleId(articleId);
    comment.setUserId(user == null ? null : user.getId());
    comment.setParentId(request.getParentId());
    comment.setGuestName(user == null ? request.getGuestName() : null);
    comment.setGuestEmail(user == null ? request.getGuestEmail() : null);
    comment.setContent(request.getContent());
    comment.setStatus(status);
    comment.setCreatedAt(LocalDateTime.now());
    comment.setUpdatedAt(LocalDateTime.now());
    commentMapper.insert(comment);

    return toDto(comment);
  }

  public List<CommentDto> listForArticle(Long articleId, User viewer, int page, int size) {
    Article article = articleMapper.findById(articleId);
    if (article == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Article not found", HttpStatus.NOT_FOUND);
    }
    if (!articleService.canViewArticle(article, viewer)) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    int offset = Math.max(0, (page - 1) * size);
    List<Comment> comments = commentMapper.listByArticle(articleId, CommentStatus.PUBLISHED, offset, size);
    return buildTree(comments);
  }

  public List<CommentDto> listForAdmin(Long articleId, CommentStatus status, int page, int size) {
    int offset = Math.max(0, (page - 1) * size);
    return commentMapper.listForAdmin(articleId, status, offset, size).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  public void delete(Long commentId, User actor, boolean isAdmin) {
    Comment comment = commentMapper.findById(commentId);
    if (comment == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Comment not found", HttpStatus.NOT_FOUND);
    }
    if (!isAdmin && (actor == null || !actor.getId().equals(comment.getUserId()))) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    commentMapper.delete(commentId);
  }

  public void updateStatus(Long commentId, CommentStatus status) {
    int updated = commentMapper.updateStatus(commentId, status, LocalDateTime.now());
    if (updated == 0) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Comment not found", HttpStatus.NOT_FOUND);
    }
  }

  public List<CommentDto> listRecentForAdmin(int limit) {
    int size = Math.max(1, limit);
    return commentMapper.listForAdmin(null, null, 0, size).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  private CommentStatus resolveInitialStatus(User user, CommentCreateRequest request) {
    String mode = siteSettingService.get("comment_review_mode");
    if (mode == null || mode.isBlank()) {
      return CommentStatus.PUBLISHED;
    }
    if ("ALL".equalsIgnoreCase(mode)) {
      return CommentStatus.PENDING;
    }
    if ("FIRST".equalsIgnoreCase(mode)) {
      if (user != null) {
        int count = commentMapper.countByUserId(user.getId());
        return count == 0 ? CommentStatus.PENDING : CommentStatus.PUBLISHED;
      }
      String email = request.getGuestEmail();
      if (email != null && !email.isBlank()) {
        int count = commentMapper.countByGuestEmail(email);
        return count == 0 ? CommentStatus.PENDING : CommentStatus.PUBLISHED;
      }
      return CommentStatus.PENDING;
    }
    return CommentStatus.PUBLISHED;
  }

  private CommentDto toDto(Comment comment) {
    CommentDto dto = new CommentDto();
    dto.setId(comment.getId());
    dto.setArticleId(comment.getArticleId());
    dto.setUserId(comment.getUserId());
    dto.setParentId(comment.getParentId());
    dto.setGuestName(comment.getGuestName());
    dto.setContent(comment.getContent());
    dto.setStatus(comment.getStatus().name());
    dto.setCreatedAt(comment.getCreatedAt());
    return dto;
  }

  private List<CommentDto> buildTree(List<Comment> comments) {
    Map<Long, CommentDto> byId = comments.stream().map(this::toDto).collect(Collectors.toMap(CommentDto::getId, c -> c));
    List<CommentDto> roots = new ArrayList<>();
    for (Comment comment : comments) {
      CommentDto dto = byId.get(comment.getId());
      if (comment.getParentId() == null) {
        roots.add(dto);
      } else {
        CommentDto parent = byId.get(comment.getParentId());
        if (parent != null) {
          if (parent.getReplies() == null) {
            parent.setReplies(new ArrayList<>());
          }
          parent.getReplies().add(dto);
        }
      }
    }
    return roots;
  }
}
