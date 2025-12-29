package com.springwithviteblog.service;

import com.springwithviteblog.mapper.SiteSettingMapper;
import org.springframework.stereotype.Service;

@Service
public class SiteSettingService {
  private final SiteSettingMapper siteSettingMapper;

  public SiteSettingService(SiteSettingMapper siteSettingMapper) {
    this.siteSettingMapper = siteSettingMapper;
  }

  public String get(String key) {
    return siteSettingMapper.findValue(key);
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    String value = siteSettingMapper.findValue(key);
    if (value == null) {
      return defaultValue;
    }
    return Boolean.parseBoolean(value);
  }

  public void set(String key, String value) {
    if (siteSettingMapper.findValue(key) == null) {
      siteSettingMapper.insert(key, value);
    } else {
      siteSettingMapper.update(key, value);
    }
  }
}
