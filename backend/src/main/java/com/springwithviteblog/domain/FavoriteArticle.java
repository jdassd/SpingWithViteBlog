package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FavoriteArticle {
    private Long id;
    private Long favoriteId;
    private Long articleId;
    private LocalDateTime createdAt;
}
