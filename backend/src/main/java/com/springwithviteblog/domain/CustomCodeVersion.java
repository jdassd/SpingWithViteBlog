package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CustomCodeVersion {
  private Long id;
  private CustomCodeType type;
  private String content;
  private Boolean enabled;
  private Long createdBy;
  private LocalDateTime createdAt;
  private Boolean isActive;
}
