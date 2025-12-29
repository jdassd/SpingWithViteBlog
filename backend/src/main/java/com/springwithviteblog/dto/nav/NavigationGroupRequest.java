package com.springwithviteblog.dto.nav;

import jakarta.validation.constraints.NotBlank;

public class NavigationGroupRequest {
  @NotBlank
  private String name;
  private Integer sortOrder;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }
}
