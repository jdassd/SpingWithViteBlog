package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class User {
  private Long id;
  private String username;
  private String email;
  private String passwordHash;
  private Role role;
  private UserStatus status;
  private Boolean isDefaultAdmin;
  private Integer failedLoginCount;
  private LocalDateTime lockedUntil;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime lastLoginAt;
}
