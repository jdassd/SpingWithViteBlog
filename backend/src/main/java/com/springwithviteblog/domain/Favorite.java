package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Favorite {
  private Long id;
  private Long userId;
  private String name;
  private String description;
  private Boolean isPublic;
  private Boolean isDefault;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
