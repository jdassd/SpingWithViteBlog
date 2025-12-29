package com.springwithviteblog.dto;

import lombok.Data;
import java.util.List;

@Data
public class SeriesDto {
    private String title;
    private String description;
    private String coverUrl;
    private String status;
    private String visibility;
    private Integer sortOrder;
    private List<Long> whitelistUserIds;
}
