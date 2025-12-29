package com.springwithviteblog.dto.user;

import com.springwithviteblog.domain.Role;
import jakarta.validation.constraints.NotNull;

public class UpdateRoleRequest {
  @NotNull
  private Role role;

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
