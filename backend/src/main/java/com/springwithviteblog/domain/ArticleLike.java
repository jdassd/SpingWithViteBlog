package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ArticleLike {
  private Long id;
  private Long articleId;
  private Long userId;
  private LocalDateTime createdAt;
}
