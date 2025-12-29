package com.springwithviteblog.dto.article;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleEditDto {
  private Long id;
  private String title;
  private String contentType;
  private String contentRaw;
  private String status;
  private String visibility;
  private Boolean rssEnabled;
  private Boolean allowIndex;
  private String summary;
  private String coverUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime publishedAt;
  private List<String> categories;
  private List<String> tags;
  private List<Long> whitelistUserIds;

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

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getContentRaw() {
    return contentRaw;
  }

  public void setContentRaw(String contentRaw) {
    this.contentRaw = contentRaw;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  public Boolean getRssEnabled() {
    return rssEnabled;
  }

  public void setRssEnabled(Boolean rssEnabled) {
    this.rssEnabled = rssEnabled;
  }

  public Boolean getAllowIndex() {
    return allowIndex;
  }

  public void setAllowIndex(Boolean allowIndex) {
    this.allowIndex = allowIndex;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getCoverUrl() {
    return coverUrl;
  }

  public void setCoverUrl(String coverUrl) {
    this.coverUrl = coverUrl;
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

  public LocalDateTime getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(LocalDateTime publishedAt) {
    this.publishedAt = publishedAt;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public List<Long> getWhitelistUserIds() {
    return whitelistUserIds;
  }

  public void setWhitelistUserIds(List<Long> whitelistUserIds) {
    this.whitelistUserIds = whitelistUserIds;
  }
}
