package com.springwithviteblog.controller;

import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.theme.ThemeDto;
import com.springwithviteblog.service.ThemeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/themes")
public class PublicThemeController {
  private final ThemeService themeService;

  public PublicThemeController(ThemeService themeService) {
    this.themeService = themeService;
  }

  @GetMapping("/active")
  public ApiResponse<ThemeDto> active() {
    return ApiResponse.ok(themeService.getActive());
  }
}
