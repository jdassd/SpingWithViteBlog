package com.springwithviteblog.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SensitiveWordService {
  private final SiteSettingService siteSettingService;

  public SensitiveWordService(SiteSettingService siteSettingService) {
    this.siteSettingService = siteSettingService;
  }

  public List<String> findMatches(String content) {
    String configured = siteSettingService.get("sensitive_words");
    if (configured == null || configured.isBlank() || content == null) {
      return List.of();
    }
    String[] words = configured.split(",");
    List<String> matches = new ArrayList<>();
    String lower = content.toLowerCase();
    for (String word : words) {
      String trimmed = word.trim();
      if (!trimmed.isEmpty() && lower.contains(trimmed.toLowerCase())) {
        matches.add(trimmed);
      }
    }
    return matches;
  }
}
