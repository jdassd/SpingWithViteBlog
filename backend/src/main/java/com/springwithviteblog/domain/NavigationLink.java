package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NavigationLink {
  private Long id;
  private Long groupId;
  private String name;
  private String url;
  private String icon;
  private String description;
  private Boolean openInNew;
  private Integer sortOrder;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
