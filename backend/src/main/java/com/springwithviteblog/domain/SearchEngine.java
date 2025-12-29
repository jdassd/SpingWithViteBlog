package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SearchEngine {
  private Long id;
  private String name;
  private String queryUrl;
  private Boolean enabled;
  private Boolean isDefault;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
