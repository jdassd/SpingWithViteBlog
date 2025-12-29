package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Album {
  private Long id;
  private String title;
  private String description;
  private Long coverPhotoId;
  private Long ownerId;
  private Visibility visibility;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
