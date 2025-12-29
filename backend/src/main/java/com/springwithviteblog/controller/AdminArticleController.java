package com.springwithviteblog.controller;

import com.springwithviteblog.domain.ArticleStatus;
import com.springwithviteblog.domain.ContentType;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.domain.Visibility;
import com.springwithviteblog.dto.article.ArticleEditDto;
import com.springwithviteblog.dto.article.ArticleRequest;
import com.springwithviteblog.dto.article.ArticleSummaryDto;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.ArticleService;
import com.springwithviteblog.service.AuditService;
import com.springwithviteblog.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/articles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminArticleController {
  private final ArticleService articleService;
  private final UserService userService;
  private final AuditService auditService;

  public AdminArticleController(ArticleService articleService, UserService userService, AuditService auditService) {
    this.articleService = articleService;
    this.userService = userService;
    this.auditService = auditService;
  }

  @GetMapping
  public ApiResponse<List<ArticleSummaryDto>> list(@RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) ArticleStatus status,
                                                   @RequestParam(required = false) ContentType contentType,
                                                   @RequestParam(required = false) Visibility visibility,
                                                   @RequestParam(required = false) Long authorId,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.ok(articleService.list(keyword, status, contentType, visibility, authorId, page, size));
  }

  @PostMapping
  public ApiResponse<ArticleEditDto> create(@Valid @RequestBody ArticleRequest request) {
    User actor = resolveActor();
    return ApiResponse.ok(articleService.getForEdit(articleService.create(actor, request).getId(), actor));
  }

  @PutMapping("/{id}")
  public ApiResponse<ArticleEditDto> update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
    User actor = resolveActor();
    articleService.update(id, actor, request);
    return ApiResponse.ok(articleService.getForEdit(id, actor));
  }

  @GetMapping("/{id}")
  public ApiResponse<ArticleEditDto> get(@PathVariable Long id) {
    return ApiResponse.ok(articleService.getForEdit(id, resolveActor()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Object> delete(@PathVariable Long id) {
    User actor = resolveActor();
    articleService.delete(id, actor);
    auditService.log(actor.getId(), "DELETE_ARTICLE", "Article", String.valueOf(id), "SUCCESS", null);
    return ApiResponse.ok(null);
  }

  private User resolveActor() {
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser == null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
    User user = userService.getById(securityUser.getId());
    if (user == null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
    return user;
  }
}
