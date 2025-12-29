package com.springwithviteblog.dto.page;

import java.time.LocalDateTime;

public class PageDto {
  private Long id;
  private String title;
  private String slug;
  private String contentType;
  private String contentHtml;
  private String contentRaw;
  private String visibility;
  private Boolean isNav;
  private Integer sortOrder;
  private String externalUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private java.util.List<Long> whitelistUserIds;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getContentHtml() {
    return contentHtml;
  }

  public void setContentHtml(String contentHtml) {
    this.contentHtml = contentHtml;
  }

  public String getContentRaw() {
    return contentRaw;
  }

  public void setContentRaw(String contentRaw) {
    this.contentRaw = contentRaw;
  }

  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
