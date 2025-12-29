package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ArticleStats {
    private Long articleId;
    private Long viewCount;
    private Long likeCount;
    private Long favoriteCount;
    private Long commentCount;
    private LocalDateTime updatedAt;
}
