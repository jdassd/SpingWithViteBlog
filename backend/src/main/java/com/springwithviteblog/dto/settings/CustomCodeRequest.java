package com.springwithviteblog.dto.settings;

import com.springwithviteblog.domain.CustomCodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CustomCodeRequest {
  @NotNull
  private CustomCodeType type;

  @NotBlank
  private String content;

  private Boolean enabled;

  public CustomCodeType getType() {
    return type;
  }

  public void setType(CustomCodeType type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }
}
