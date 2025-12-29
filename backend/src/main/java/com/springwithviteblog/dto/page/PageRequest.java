package com.springwithviteblog.dto.page;

import com.springwithviteblog.domain.ContentType;
import com.springwithviteblog.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PageRequest {
  @NotBlank
  private String title;

  @NotBlank
  private String slug;

  private ContentType contentType;
  private String contentRaw;

  @NotNull
  private Visibility visibility;

  private Boolean isNav;
  private Integer sortOrder;
  private String externalUrl;
  private java.util.List<Long> whitelistUserIds;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public ContentType getContentType() {
    return contentType;
  }

  public void setContentType(ContentType contentType) {
    this.contentType = contentType;
  }

  public String getContentRaw() {
    return contentRaw;
  }

  public void setContentRaw(String contentRaw) {
    this.contentRaw = contentRaw;
  }

  public Visibility getVisibility() {
    return visibility;
  }

  public void setVisibility(Visibility visibility) {
    this.visibility = visibility;
  }

  public Boolean getIsNav() {
    return isNav;
  }

  public void setIsNav(Boolean isNav) {
    this.isNav = isNav;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  public String getExternalUrl() {
    return externalUrl;
  }

  public void setExternalUrl(String externalUrl) {
    this.externalUrl = externalUrl;
  }

  public java.util.List<Long> getWhitelistUserIds() {
    return whitelistUserIds;
  }

  public void setWhitelistUserIds(java.util.List<Long> whitelistUserIds) {
    this.whitelistUserIds = whitelistUserIds;
  }
}
