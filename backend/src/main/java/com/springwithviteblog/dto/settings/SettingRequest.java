package com.springwithviteblog.dto.settings;

import jakarta.validation.constraints.NotBlank;

public class SettingRequest {
  @NotBlank
  private String key;

  private String value;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
