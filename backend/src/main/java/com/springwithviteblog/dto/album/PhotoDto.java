package com.springwithviteblog.dto.album;

import java.time.LocalDateTime;
import java.util.List;

public class PhotoDto {
  private Long id;
  private Long albumId;
  private String filename;
  private String originalPath;
  private String thumbnailPath;
  private String externalUrl;
  private String syncStatus;
  private String syncError;
  private String exifJson;
  private LocalDateTime takenAt;
  private LocalDateTime createdAt;
  private List<String> tags;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAlbumId() {
    return albumId;
  }

  public void setAlbumId(Long albumId) {
    this.albumId = albumId;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getOriginalPath() {
    return originalPath;
  }

  public void setOriginalPath(String originalPath) {
    this.originalPath = originalPath;
  }

  public String getThumbnailPath() {
    return thumbnailPath;
  }

  public void setThumbnailPath(String thumbnailPath) {
    this.thumbnailPath = thumbnailPath;
  }

  public String getExternalUrl() {
    return externalUrl;
  }

  public void setExternalUrl(String externalUrl) {
    this.externalUrl = externalUrl;
  }

  public String getSyncStatus() {
    return syncStatus;
  }

  public void setSyncStatus(String syncStatus) {
    this.syncStatus = syncStatus;
  }

  public String getSyncError() {
    return syncError;
  }

  public void setSyncError(String syncError) {
    this.syncError = syncError;
  }

  public String getExifJson() {
    return exifJson;
  }

  public void setExifJson(String exifJson) {
    this.exifJson = exifJson;
  }

  public LocalDateTime getTakenAt() {
    return takenAt;
  }

  public void setTakenAt(LocalDateTime takenAt) {
    this.takenAt = takenAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }
}
