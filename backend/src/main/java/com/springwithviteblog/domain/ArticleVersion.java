package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ArticleVersion {
    private Long id;
    private Long articleId;
    private Integer versionNumber;
    private String title;
    private String contentRaw;
    private String contentHtml;
    private LocalDateTime createdAt;
}
