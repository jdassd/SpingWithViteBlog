package com.springwithviteblog.dto.nav;

import jakarta.validation.constraints.NotBlank;

public class SearchEngineRequest {
  @NotBlank
  private String name;

  @NotBlank
  private String queryUrl;

  private Boolean enabled;
  private Boolean isDefault;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getQueryUrl() {
    return queryUrl;
  }

  public void setQueryUrl(String queryUrl) {
    this.queryUrl = queryUrl;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Boolean getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(Boolean isDefault) {
    this.isDefault = isDefault;
  }
}
