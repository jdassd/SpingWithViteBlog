package com.springwithviteblog.dto;

import lombok.Data;

@Data
public class RankingDto {
    private Long articleId;
    private String title;
    private String coverUrl;
    private Long authorId;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long favoriteCount;
    private Long score;
}
