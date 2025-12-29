package com.springwithviteblog.dto.album;

import com.springwithviteblog.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AlbumRequest {
  @NotBlank
  private String title;

  private String description;

  @NotNull
  private Visibility visibility;

  private java.util.List<Long> whitelistUserIds;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Visibility getVisibility() {
    return visibility;
  }

  public void setVisibility(Visibility visibility) {
    this.visibility = visibility;
  }

  public java.util.List<Long> getWhitelistUserIds() {
    return whitelistUserIds;
  }

  public void setWhitelistUserIds(java.util.List<Long> whitelistUserIds) {
    this.whitelistUserIds = whitelistUserIds;
  }
}
