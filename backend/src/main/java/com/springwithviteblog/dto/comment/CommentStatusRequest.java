package com.springwithviteblog.dto.comment;

import com.springwithviteblog.domain.CommentStatus;
import jakarta.validation.constraints.NotNull;

public class CommentStatusRequest {
  @NotNull
  private CommentStatus status;

  public CommentStatus getStatus() {
    return status;
  }

  public void setStatus(CommentStatus status) {
    this.status = status;
  }
}
