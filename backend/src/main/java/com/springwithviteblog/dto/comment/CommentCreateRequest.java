package com.springwithviteblog.dto.comment;

import jakarta.validation.constraints.NotBlank;

public class CommentCreateRequest {
  @NotBlank
  private String content;

  private Long parentId;
  private String guestName;
  private String guestEmail;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getGuestName() {
    return guestName;
  }

  public void setGuestName(String guestName) {
    this.guestName = guestName;
  }

  public String getGuestEmail() {
    return guestEmail;
  }

  public void setGuestEmail(String guestEmail) {
    this.guestEmail = guestEmail;
  }
}
