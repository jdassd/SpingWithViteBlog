package com.springwithviteblog.dto.comment;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDto {
  private Long id;
  private Long articleId;
  private Long userId;
  private Long parentId;
  private String guestName;
  private String content;
  private String status;
  private LocalDateTime createdAt;
  private List<CommentDto> replies;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getArticleId() {
    return articleId;
  }

  public void setArticleId(Long articleId) {
    this.articleId = articleId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public List<CommentDto> getReplies() {
    return replies;
  }

  public void setReplies(List<CommentDto> replies) {
    this.replies = replies;
  }
}
