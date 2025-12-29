package com.springwithviteblog.dto;

import lombok.Data;

@Data
public class FavoriteDto {
    private String name;
    private String description;
    private Boolean isPublic;
}
