package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Article {
  private Long id;
  private Long authorId;
  private String title;
  private ContentType contentType;
  private String contentRaw;
  private String contentHtml;
  private String summary;
  private String coverUrl;
  private ArticleStatus status;
  private Visibility visibility;
  private Boolean rssEnabled;
  private Boolean allowIndex;
  private Long seriesId;
  private Integer seriesOrder;
  private LocalDateTime scheduledAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime publishedAt;
}
