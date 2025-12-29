package com.springwithviteblog.controller;

import com.springwithviteblog.domain.User;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.RssService;
import com.springwithviteblog.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RssController {
  private final RssService rssService;
  private final UserService userService;

  public RssController(RssService rssService, UserService userService) {
    this.rssService = rssService;
    this.userService = userService;
  }

  @GetMapping(value = "/rss/public", produces = MediaType.APPLICATION_XML_VALUE)
  public String publicFeed() {
    return rssService.generatePublicFeed();
  }

  @GetMapping(value = "/rss/private", produces = MediaType.APPLICATION_XML_VALUE)
  public String privateFeed(@RequestParam("token") String token) {
    return rssService.generatePrivateFeed(token);
  }

  @PostMapping("/api/rss/token")
  public ApiResponse<String> createToken(@RequestParam(defaultValue = "30") int days) {
    User user = currentUser();
    String token = rssService.createToken(user, days);
    return ApiResponse.ok(token);
  }

  @DeleteMapping("/api/rss/token")
  public ApiResponse<Object> revokeToken(@RequestParam("token") String token) {
    currentUser();
    rssService.revokeToken(token);
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
}
