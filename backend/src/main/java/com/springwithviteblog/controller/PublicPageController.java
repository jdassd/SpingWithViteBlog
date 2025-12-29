package com.springwithviteblog.controller;

import com.springwithviteblog.domain.User;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.page.PageDto;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.PageService;
import com.springwithviteblog.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/pages")
public class PublicPageController {
  private final PageService pageService;
  private final UserService userService;

  public PublicPageController(PageService pageService, UserService userService) {
    this.pageService = pageService;
    this.userService = userService;
  }

  @GetMapping("/{slug}")
  public ApiResponse<PageDto> getBySlug(@PathVariable String slug) {
    return ApiResponse.ok(pageService.getBySlug(slug, currentUser()));
  }

  @GetMapping("/nav")
  public ApiResponse<List<PageDto>> navPages() {
    return ApiResponse.ok(pageService.listNav(currentUser()));
  }

  private User currentUser() {
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser == null) {
      return null;
    }
    return userService.getById(securityUser.getId());
  }
}
