package com.springwithviteblog.dto;

import lombok.Data;

@Data
public class RankingConfigDto {
    private Integer viewWeight;
    private Integer likeWeight;
    private Integer commentWeight;
    private Integer favoriteWeight;
}
