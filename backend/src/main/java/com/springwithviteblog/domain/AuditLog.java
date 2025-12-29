package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AuditLog {
  private Long id;
  private Long userId;
  private String action;
  private String resourceType;
  private String resourceId;
  private String result;
  private String message;
  private LocalDateTime createdAt;
}
