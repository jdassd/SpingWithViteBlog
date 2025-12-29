package com.springwithviteblog.dto.user;

import com.springwithviteblog.domain.UserStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {
  @NotNull
  private UserStatus status;

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }
}
