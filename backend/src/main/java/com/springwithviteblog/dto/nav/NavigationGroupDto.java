package com.springwithviteblog.dto.nav;

import java.util.List;

public class NavigationGroupDto {
  private Long id;
  private String name;
  private Integer sortOrder;
  private List<NavigationLinkDto> links;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public List<NavigationLinkDto> getLinks() {
    return links;
  }

  public void setLinks(List<NavigationLinkDto> links) {
    this.links = links;
  }
}
