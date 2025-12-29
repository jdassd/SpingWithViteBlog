package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FriendLink {
    private Long id;
    private String name;
    private String url;
    private String description;
    private String logoUrl;
    private String category;
    private Integer sortOrder;
    private Boolean isEnabled;
    private LinkStatus status;
    private LocalDateTime lastCheckAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
