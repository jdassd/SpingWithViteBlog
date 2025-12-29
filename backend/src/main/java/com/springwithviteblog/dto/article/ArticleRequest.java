package com.springwithviteblog.dto.article;

import com.springwithviteblog.domain.ArticleStatus;
import com.springwithviteblog.domain.ContentType;
import com.springwithviteblog.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ArticleRequest {
  @NotBlank
  private String title;

  @NotNull
  private ContentType contentType;

  @NotBlank
  private String contentRaw;

  @NotNull
  private ArticleStatus status;

  @NotNull
  private Visibility visibility;

  private Boolean rssEnabled;
  private Boolean allowIndex;
  private String summary;
  private String coverUrl;
  private List<String> categories;
  private List<String> tags;
  private List<Long> whitelistUserIds;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public ArticleStatus getStatus() {
    return status;
  }

  public void setStatus(ArticleStatus status) {
    this.status = status;
  }

  public Visibility getVisibility() {
    return visibility;
  }

  public void setVisibility(Visibility visibility) {
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
