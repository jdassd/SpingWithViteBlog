package com.springwithviteblog.dto.album;

import java.time.LocalDateTime;

public class AlbumDto {
  private Long id;
  private String title;
  private String description;
  private Long coverPhotoId;
  private Long ownerId;
  private String visibility;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getCoverPhotoId() {
    return coverPhotoId;
  }

  public void setCoverPhotoId(Long coverPhotoId) {
    this.coverPhotoId = coverPhotoId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
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
