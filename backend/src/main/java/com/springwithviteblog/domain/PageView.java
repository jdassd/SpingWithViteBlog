package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PageView {
    private Long id;
    private Long articleId;
    private String pagePath;
    private String visitorIp;
    private Long userId;
    private String userAgent;
    private String referer;
    private LocalDateTime createdAt;
}
