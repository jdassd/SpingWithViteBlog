package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Theme {
  private Long id;
  private String name;
  private String version;
  private String author;
  private String description;
  private String storagePath;
  private String themeJson;
  private Boolean isActive;
  private String configJson;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
