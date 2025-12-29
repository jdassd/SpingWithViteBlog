package com.springwithviteblog.dto.theme;

import jakarta.validation.constraints.NotBlank;

public class ThemeConfigRequest {
  @NotBlank
  private String configJson;

  public String getConfigJson() {
    return configJson;
  }

  public void setConfigJson(String configJson) {
    this.configJson = configJson;
  }
}
