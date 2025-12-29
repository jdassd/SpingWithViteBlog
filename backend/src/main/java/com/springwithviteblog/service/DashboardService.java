package com.springwithviteblog.service;

import com.springwithviteblog.domain.ArticleStatus;
import com.springwithviteblog.domain.AuditLog;
import com.springwithviteblog.domain.CommentStatus;
import com.springwithviteblog.dto.audit.AuditLogDto;
import com.springwithviteblog.dto.dashboard.DashboardStatsDto;
import com.springwithviteblog.mapper.AlbumMapper;
import com.springwithviteblog.mapper.ArticleMapper;
import com.springwithviteblog.mapper.CommentMapper;
import com.springwithviteblog.mapper.PhotoMapper;
import com.springwithviteblog.mapper.UserMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
  private final ArticleMapper articleMapper;
  private final CommentMapper commentMapper;
  private final UserMapper userMapper;
  private final AlbumMapper albumMapper;
  private final PhotoMapper photoMapper;
  private final ArticleService articleService;
  private final CommentService commentService;
  private final AuditService auditService;

  public DashboardService(ArticleMapper articleMapper,
                          CommentMapper commentMapper,
                          UserMapper userMapper,
                          AlbumMapper albumMapper,
                          PhotoMapper photoMapper,
                          ArticleService articleService,
                          CommentService commentService,
                          AuditService auditService) {
    this.articleMapper = articleMapper;
    this.commentMapper = commentMapper;
    this.userMapper = userMapper;
    this.albumMapper = albumMapper;
    this.photoMapper = photoMapper;
    this.articleService = articleService;
    this.commentService = commentService;
    this.auditService = auditService;
  }

  public DashboardStatsDto getStats() {
    DashboardStatsDto dto = new DashboardStatsDto();
    dto.setArticlesTotal(articleMapper.countAll());
    dto.setArticlesPublished(articleMapper.countByStatus(ArticleStatus.PUBLISHED));
    dto.setArticlesDraft(articleMapper.countByStatus(ArticleStatus.DRAFT));
    dto.setArticlesArchived(articleMapper.countByStatus(ArticleStatus.ARCHIVED));
    dto.setCommentsTotal(commentMapper.countAll());
    dto.setCommentsPending(commentMapper.countByStatus(CommentStatus.PENDING));
    dto.setCommentsPublished(commentMapper.countByStatus(CommentStatus.PUBLISHED));
    dto.setCommentsBlocked(commentMapper.countByStatus(CommentStatus.BLOCKED));
    dto.setUsersTotal(userMapper.countAll());
    dto.setAlbumsTotal(albumMapper.countAll());
    dto.setPhotosTotal(photoMapper.countAll());
    dto.setRecentArticles(articleService.listRecent(5));
    dto.setRecentComments(commentService.listRecentForAdmin(5));
    dto.setRecentAudits(toAuditDtos(auditService.list(null, null, 1, 8)));
    return dto;
  }

  private List<AuditLogDto> toAuditDtos(List<AuditLog> logs) {
    return logs.stream().map(this::toDto).collect(Collectors.toList());
  }

  private AuditLogDto toDto(AuditLog log) {
    AuditLogDto dto = new AuditLogDto();
    dto.setId(log.getId());
    dto.setUserId(log.getUserId());
    dto.setAction(log.getAction());
    dto.setResourceType(log.getResourceType());
    dto.setResourceId(log.getResourceId());
    dto.setResult(log.getResult());
    dto.setMessage(log.getMessage());
    dto.setCreatedAt(log.getCreatedAt());
    return dto;
  }
}
