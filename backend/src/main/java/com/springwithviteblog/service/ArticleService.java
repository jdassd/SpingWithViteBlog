package com.springwithviteblog.service;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.ArticleStatus;
import com.springwithviteblog.domain.ContentType;
import com.springwithviteblog.domain.Role;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.domain.Visibility;
import com.springwithviteblog.dto.article.ArticleDetailDto;
import com.springwithviteblog.dto.article.ArticleEditDto;
import com.springwithviteblog.dto.article.ArticleRequest;
import com.springwithviteblog.dto.article.ArticleSummaryDto;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.ArticleMapper;
import com.springwithviteblog.mapper.ArticleWhitelistMapper;
import com.springwithviteblog.mapper.CategoryMapper;
import com.springwithviteblog.mapper.TagMapper;
import com.springwithviteblog.util.SlugUtils;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {
  private final ArticleMapper articleMapper;
  private final CategoryMapper categoryMapper;
  private final TagMapper tagMapper;
  private final ArticleWhitelistMapper whitelistMapper;
  private final ContentProcessor contentProcessor;
  private final SiteSettingService siteSettingService;

  public ArticleService(ArticleMapper articleMapper,
                        CategoryMapper categoryMapper,
                        TagMapper tagMapper,
                        ArticleWhitelistMapper whitelistMapper,
                        ContentProcessor contentProcessor,
                        SiteSettingService siteSettingService) {
    this.articleMapper = articleMapper;
    this.categoryMapper = categoryMapper;
    this.tagMapper = tagMapper;
    this.whitelistMapper = whitelistMapper;
    this.contentProcessor = contentProcessor;
    this.siteSettingService = siteSettingService;
  }

  @Transactional
  public Article create(User author, ArticleRequest request) {
    validateVisibility(request.getVisibility(), author);

    Article article = new Article();
    article.setAuthorId(author.getId());
    article.setTitle(request.getTitle());
    article.setContentType(request.getContentType());
    article.setContentRaw(request.getContentRaw());
    article.setContentHtml(contentProcessor.toSafeHtml(request.getContentType(), request.getContentRaw()));
    article.setSummary(resolveSummary(request));
    article.setCoverUrl(request.getCoverUrl());
    article.setStatus(request.getStatus());
    article.setVisibility(request.getVisibility());
    article.setRssEnabled(resolveRssEnabled(request));
    article.setAllowIndex(request.getAllowIndex() == null ? true : request.getAllowIndex());
    article.setCreatedAt(LocalDateTime.now());
    article.setUpdatedAt(LocalDateTime.now());
    article.setPublishedAt(request.getStatus() == ArticleStatus.PUBLISHED ? LocalDateTime.now() : null);

    articleMapper.insert(article);
    upsertCategories(article.getId(), request.getCategories());
    upsertTags(article.getId(), request.getTags());
    updateWhitelist(article.getId(), request.getVisibility(), request.getWhitelistUserIds());
    return article;
  }

  @Transactional
  public Article update(Long articleId, User editor, ArticleRequest request) {
    Article existing = articleMapper.findById(articleId);
    if (existing == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Article not found", HttpStatus.NOT_FOUND);
    }
    if (!isAdmin(editor) && !Objects.equals(existing.getAuthorId(), editor.getId())) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    if (!isAdmin(editor) && existing.getStatus() != ArticleStatus.DRAFT) {
      throw new ApiException(ErrorCodes.OPERATION_NOT_ALLOWED, "Only drafts can be edited", HttpStatus.BAD_REQUEST);
    }

    validateVisibility(request.getVisibility(), editor);

    existing.setTitle(request.getTitle());
    existing.setContentType(request.getContentType());
    existing.setContentRaw(request.getContentRaw());
    existing.setContentHtml(contentProcessor.toSafeHtml(request.getContentType(), request.getContentRaw()));
    existing.setSummary(resolveSummary(request));
    existing.setCoverUrl(request.getCoverUrl());
    existing.setStatus(request.getStatus());
    existing.setVisibility(request.getVisibility());
    existing.setRssEnabled(resolveRssEnabled(request));
    existing.setAllowIndex(request.getAllowIndex() == null ? true : request.getAllowIndex());
    existing.setUpdatedAt(LocalDateTime.now());
    if (request.getStatus() == ArticleStatus.PUBLISHED && existing.getPublishedAt() == null) {
      existing.setPublishedAt(LocalDateTime.now());
    }

    articleMapper.update(existing);
    upsertCategories(existing.getId(), request.getCategories());
    upsertTags(existing.getId(), request.getTags());
    updateWhitelist(existing.getId(), request.getVisibility(), request.getWhitelistUserIds());
    return existing;
  }

  public ArticleDetailDto getDetail(Long id, User viewer) {
    Article article = articleMapper.findById(id);
    if (article == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Article not found", HttpStatus.NOT_FOUND);
    }
    if (!canViewArticle(article, viewer)) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    ArticleDetailDto dto = new ArticleDetailDto();
    dto.setId(article.getId());
    dto.setTitle(article.getTitle());
    dto.setAuthorId(article.getAuthorId());
    dto.setContentType(article.getContentType().name());
    dto.setRenderedContent(article.getContentHtml());
    dto.setVisibility(article.getVisibility().name());
    dto.setStatus(article.getStatus().name());
    dto.setCoverUrl(article.getCoverUrl());
    dto.setCreatedAt(article.getCreatedAt());
    dto.setUpdatedAt(article.getUpdatedAt());
    dto.setPublishedAt(article.getPublishedAt());
    dto.setCategories(categoryMapper.findByArticleId(article.getId()).stream().map(c -> c.getName()).collect(Collectors.toList()));
    dto.setTags(tagMapper.findByArticleId(article.getId()).stream().map(t -> t.getName()).collect(Collectors.toList()));
    return dto;
  }

  public ArticleEditDto getForEdit(Long id, User editor) {
    Article article = articleMapper.findById(id);
    if (article == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Article not found", HttpStatus.NOT_FOUND);
    }
    if (!isAdmin(editor) && !Objects.equals(article.getAuthorId(), editor.getId())) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    ArticleEditDto dto = new ArticleEditDto();
    dto.setId(article.getId());
    dto.setTitle(article.getTitle());
    dto.setContentType(article.getContentType().name());
    dto.setContentRaw(article.getContentRaw());
    dto.setStatus(article.getStatus().name());
    dto.setVisibility(article.getVisibility().name());
    dto.setRssEnabled(article.getRssEnabled());
    dto.setAllowIndex(article.getAllowIndex());
    dto.setSummary(article.getSummary());
    dto.setCoverUrl(article.getCoverUrl());
    dto.setCreatedAt(article.getCreatedAt());
    dto.setUpdatedAt(article.getUpdatedAt());
    dto.setPublishedAt(article.getPublishedAt());
    dto.setCategories(categoryMapper.findByArticleId(article.getId()).stream().map(c -> c.getName()).collect(Collectors.toList()));
    dto.setTags(tagMapper.findByArticleId(article.getId()).stream().map(t -> t.getName()).collect(Collectors.toList()));
    dto.setWhitelistUserIds(whitelistMapper.findUserIds(article.getId()));
    return dto;
  }

  public List<ArticleSummaryDto> list(String keyword, ArticleStatus status, ContentType contentType, Visibility visibility,
                                      Long authorId, int page, int size) {
    int offset = Math.max(0, (page - 1) * size);
    List<Article> articles = articleMapper.search(keyword, status, contentType, visibility, authorId, offset, size);
    return articles.stream().map(this::toSummary).collect(Collectors.toList());
  }

  public List<ArticleSummaryDto> listPublic(int page, int size, User viewer, String categorySlug, String tagSlug) {
    int offset = Math.max(0, (page - 1) * size);
    List<Article> articles = articleMapper.search(null, ArticleStatus.PUBLISHED, null, null, null, offset, size);
    return articles.stream()
        .filter(article -> canViewArticle(article, viewer))
        .map(this::toSummary)
        .filter(summary -> matchesTaxonomy(summary, categorySlug, tagSlug))
        .collect(Collectors.toList());
  }

  public List<ArticleSummaryDto> listRecent(int limit) {
    int size = Math.max(1, limit);
    List<Article> articles = articleMapper.search(null, null, null, null, null, 0, size);
    return articles.stream().map(this::toSummary).collect(Collectors.toList());
  }

  private boolean matchesTaxonomy(ArticleSummaryDto summary, String categorySlug, String tagSlug) {
    if (categorySlug == null || categorySlug.isBlank()) {
      if (tagSlug == null || tagSlug.isBlank()) {
        return true;
      }
    }
    boolean categoryOk = true;
    if (categorySlug != null && !categorySlug.isBlank()) {
      categoryOk = summary.getCategories() != null && summary.getCategories().stream()
          .anyMatch(category -> SlugUtils.toSlug(category).equalsIgnoreCase(categorySlug));
    }
    boolean tagOk = true;
    if (tagSlug != null && !tagSlug.isBlank()) {
      tagOk = summary.getTags() != null && summary.getTags().stream()
          .anyMatch(tag -> SlugUtils.toSlug(tag).equalsIgnoreCase(tagSlug));
    }
    return categoryOk && tagOk;
  }

  public List<ArticleSummaryDto> searchPublic(String keyword, User viewer, int page, int size) {
    int offset = Math.max(0, (page - 1) * size);
    List<Article> articles = articleMapper.search(keyword, ArticleStatus.PUBLISHED, null, null, null, offset, size);
    return articles.stream()
        .filter(article -> canViewArticle(article, viewer))
        .map(this::toSummary)
        .collect(Collectors.toList());
  }

  @Transactional
  public void delete(Long id, User actor) {
    Article article = articleMapper.findById(id);
    if (article == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Article not found", HttpStatus.NOT_FOUND);
    }
    if (!isAdmin(actor) && !Objects.equals(article.getAuthorId(), actor.getId())) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    articleMapper.delete(id);
  }

  public List<Article> listPublishedPublic() {
    return articleMapper.findPublishedPublic();
  }

  public List<Article> listPublishedForUser(User viewer) {
    return articleMapper.findPublished()
        .stream()
        .filter(article -> canViewArticle(article, viewer))
        .collect(Collectors.toList());
  }

  private ArticleSummaryDto toSummary(Article article) {
    ArticleSummaryDto dto = new ArticleSummaryDto();
    dto.setId(article.getId());
    dto.setTitle(article.getTitle());
    dto.setSummary(article.getSummary());
    dto.setAuthorId(article.getAuthorId());
    dto.setContentType(article.getContentType().name());
    dto.setStatus(article.getStatus().name());
    dto.setVisibility(article.getVisibility().name());
    dto.setCoverUrl(article.getCoverUrl());
    dto.setCreatedAt(article.getCreatedAt());
    dto.setPublishedAt(article.getPublishedAt());
    dto.setCategories(categoryMapper.findByArticleId(article.getId()).stream().map(c -> c.getName()).collect(Collectors.toList()));
    dto.setTags(tagMapper.findByArticleId(article.getId()).stream().map(t -> t.getName()).collect(Collectors.toList()));
    return dto;
  }

  private void upsertCategories(Long articleId, List<String> categories) {
    categoryMapper.deleteArticleCategories(articleId);
    if (categories == null) {
      return;
    }
    for (String name : categories) {
      if (name == null || name.isBlank()) {
        continue;
      }
      String slug = SlugUtils.toSlug(name);
      com.springwithviteblog.domain.Category category = categoryMapper.findBySlug(slug);
      if (category == null) {
        category = new com.springwithviteblog.domain.Category();
        category.setName(name.trim());
        category.setSlug(slug);
        categoryMapper.insert(category);
      }
      categoryMapper.insertArticleCategory(articleId, category.getId());
    }
  }

  private void upsertTags(Long articleId, List<String> tags) {
    tagMapper.deleteArticleTags(articleId);
    if (tags == null) {
      return;
    }
    for (String name : tags) {
      if (name == null || name.isBlank()) {
        continue;
      }
      String slug = SlugUtils.toSlug(name);
      com.springwithviteblog.domain.Tag tag = tagMapper.findBySlug(slug);
      if (tag == null) {
        tag = new com.springwithviteblog.domain.Tag();
        tag.setName(name.trim());
        tag.setSlug(slug);
        tagMapper.insert(tag);
      }
      tagMapper.insertArticleTag(articleId, tag.getId());
    }
  }

  private void updateWhitelist(Long articleId, Visibility visibility, List<Long> userIds) {
    whitelistMapper.deleteByArticleId(articleId);
    if (visibility != Visibility.WHITELIST) {
      return;
    }
    if (userIds == null || userIds.isEmpty()) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Whitelist users required", HttpStatus.BAD_REQUEST);
    }
    for (Long userId : userIds) {
      whitelistMapper.insert(articleId, userId);
    }
  }

  private String resolveSummary(ArticleRequest request) {
    if (request.getSummary() != null && !request.getSummary().isBlank()) {
      return request.getSummary();
    }
    return contentProcessor.toSummary(request.getContentType(), request.getContentRaw(), 200);
  }

  private boolean resolveRssEnabled(ArticleRequest request) {
    if (request.getRssEnabled() != null) {
      return request.getRssEnabled();
    }
    return request.getVisibility() == Visibility.PUBLIC;
  }

  private void validateVisibility(Visibility visibility, User user) {
    if (visibility == Visibility.ADMIN_ONLY && !isAdmin(user)) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Admin only visibility not allowed", HttpStatus.FORBIDDEN);
    }
  }

  public boolean canViewArticle(Article article, User viewer) {
    if (article.getVisibility() == Visibility.PUBLIC) {
      return true;
    }
    if (viewer == null) {
      return false;
    }
    if (isAdmin(viewer)) {
      boolean adminCanViewPrivate = siteSettingService.getBoolean("admin_can_view_private", true);
      if (article.getVisibility() == Visibility.PRIVATE) {
        return adminCanViewPrivate || Objects.equals(article.getAuthorId(), viewer.getId());
      }
      return true;
    }
    if (article.getVisibility() == Visibility.LOGIN_ONLY) {
      return true;
    }
    if (article.getVisibility() == Visibility.PRIVATE) {
      return Objects.equals(article.getAuthorId(), viewer.getId());
    }
    if (article.getVisibility() == Visibility.WHITELIST) {
      return whitelistMapper.exists(article.getId(), viewer.getId()) > 0;
    }
    return false;
  }

  private boolean isAdmin(User user) {
    return user.getRole() == Role.ADMIN;
  }
}
