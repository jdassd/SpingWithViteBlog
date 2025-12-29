package com.springwithviteblog.controller;

import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.settings.SettingRequest;
import com.springwithviteblog.service.SiteSettingService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SiteSettingController {
  private final SiteSettingService siteSettingService;

  public SiteSettingController(SiteSettingService siteSettingService) {
    this.siteSettingService = siteSettingService;
  }

  @PostMapping
  public ApiResponse<Object> set(@Valid @RequestBody SettingRequest request) {
    siteSettingService.set(request.getKey(), request.getValue());
    return ApiResponse.ok(null);
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
