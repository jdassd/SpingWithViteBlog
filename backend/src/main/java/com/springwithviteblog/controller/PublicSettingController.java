package com.springwithviteblog.controller;

import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.service.SiteSettingService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/settings")
public class PublicSettingController {
  private final SiteSettingService siteSettingService;

  public PublicSettingController(SiteSettingService siteSettingService) {
    this.siteSettingService = siteSettingService;
  }

  @GetMapping
  public ApiResponse<Map<String, String>> get(@RequestParam List<String> keys) {
    Map<String, String> result = new HashMap<>();
    for (String key : keys) {
      result.put(key, siteSettingService.get(key));
    }
    return ApiResponse.ok(result);
  }
}
