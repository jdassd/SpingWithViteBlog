package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Comment {
  private Long id;
  private Long articleId;
  private Long userId;
  private Long parentId;
  private String guestName;
  private String guestEmail;
  private String content;
  private CommentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
