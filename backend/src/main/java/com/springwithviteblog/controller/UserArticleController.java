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
import com.springwithviteblog.service.SiteSettingService;
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
@RequestMapping("/api/user/articles")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserArticleController {
  private final ArticleService articleService;
  private final UserService userService;
  private final SiteSettingService siteSettingService;

  public UserArticleController(ArticleService articleService, UserService userService, SiteSettingService siteSettingService) {
    this.articleService = articleService;
    this.userService = userService;
    this.siteSettingService = siteSettingService;
  }

  @GetMapping
  public ApiResponse<List<ArticleSummaryDto>> list(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
    User user = currentUser();
    return ApiResponse.ok(articleService.list(null, null, null, null, user.getId(), page, size));
  }

  @PostMapping
  public ApiResponse<ArticleEditDto> create(@Valid @RequestBody ArticleRequest request) {
    User user = currentUser();
    ensureUserPostAllowed(user);
    ArticleEditDto dto = articleService.getForEdit(articleService.create(user, request).getId(), user);
    return ApiResponse.ok(dto);
  }

  @PutMapping("/{id}")
  public ApiResponse<ArticleEditDto> update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
    User user = currentUser();
    ensureUserPostAllowed(user);
    articleService.update(id, user, request);
    return ApiResponse.ok(articleService.getForEdit(id, user));
  }

  @GetMapping("/{id}")
  public ApiResponse<ArticleEditDto> get(@PathVariable Long id) {
    return ApiResponse.ok(articleService.getForEdit(id, currentUser()));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Object> delete(@PathVariable Long id) {
    articleService.delete(id, currentUser());
    return ApiResponse.ok(null);
  }

  private User currentUser() {
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

  private void ensureUserPostAllowed(User user) {
    if (user.getRole().name().equals("ADMIN")) {
      return;
    }
    boolean allowed = siteSettingService.getBoolean("allow_user_post", false);
    if (!allowed) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "User posting disabled", HttpStatus.FORBIDDEN);
    }
  }
}
