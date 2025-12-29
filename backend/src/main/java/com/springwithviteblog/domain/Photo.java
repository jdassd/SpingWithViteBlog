package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Photo {
  private Long id;
  private Long albumId;
  private String filename;
  private String originalPath;
  private String thumbnailPath;
  private String externalUrl;
  private SyncStatus syncStatus;
  private String syncError;
  private String exifJson;
  private LocalDateTime takenAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
