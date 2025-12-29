package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Series {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private SeriesStatus status;
    private Visibility visibility;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
