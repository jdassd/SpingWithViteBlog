package com.springwithviteblog.dto;

import lombok.Data;

@Data
public class FriendLinkDto {
    private String name;
    private String url;
    private String description;
    private String logoUrl;
    private String category;
    private Integer sortOrder;
    private Boolean isEnabled;
}
