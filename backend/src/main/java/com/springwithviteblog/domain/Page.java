package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Page {
  private Long id;
  private String title;
  private String slug;
  private ContentType contentType;
  private String contentRaw;
  private String contentHtml;
  private Visibility visibility;
  private Boolean isNav;
  private Integer sortOrder;
  private String externalUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
