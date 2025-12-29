package com.springwithviteblog.controller;

import com.springwithviteblog.domain.User;
import com.springwithviteblog.dto.article.ArticleDetailDto;
import com.springwithviteblog.dto.article.ArticleSummaryDto;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.ArticleService;
import com.springwithviteblog.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/articles")
public class PublicArticleController {
  private final ArticleService articleService;
  private final UserService userService;

  public PublicArticleController(ArticleService articleService, UserService userService) {
    this.articleService = articleService;
    this.userService = userService;
  }

  @GetMapping
  public ApiResponse<List<ArticleSummaryDto>> list(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String category,
                                                   @RequestParam(required = false) String tag) {
    User viewer = null;
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser != null) {
      viewer = userService.getById(securityUser.getId());
    }
    return ApiResponse.ok(articleService.listPublic(page, size, viewer, category, tag));
  }

  @GetMapping("/{id}")
  public ApiResponse<ArticleDetailDto> detail(@PathVariable Long id) {
    User viewer = null;
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser != null) {
      viewer = userService.getById(securityUser.getId());
    }
    return ApiResponse.ok(articleService.getDetail(id, viewer));
  }
}
