package com.springwithviteblog.controller;

import com.springwithviteblog.domain.User;
import com.springwithviteblog.dto.article.ArticleSummaryDto;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.ArticleService;
import com.springwithviteblog.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/search")
public class SearchController {
  private final ArticleService articleService;
  private final UserService userService;

  public SearchController(ArticleService articleService, UserService userService) {
    this.articleService = articleService;
    this.userService = userService;
  }

  @GetMapping("/articles")
  public ApiResponse<List<ArticleSummaryDto>> searchArticles(@RequestParam String keyword,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.ok(articleService.searchPublic(keyword, currentUser(), page, size));
  }

  private User currentUser() {
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser == null) {
      return null;
    }
    return userService.getById(securityUser.getId());
  }
}
