package com.springwithviteblog.dto.nav;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NavigationLinkRequest {
  @NotNull
  private Long groupId;

  @NotBlank
  private String name;

  @NotBlank
  private String url;

  private String icon;
  private String description;
  private Boolean openInNew;
  private Integer sortOrder;

  public Long getGroupId() {
    return groupId;
  }

  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getOpenInNew() {
    return openInNew;
  }

  public void setOpenInNew(Boolean openInNew) {
    this.openInNew = openInNew;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }
}
