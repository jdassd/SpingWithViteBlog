package com.springwithviteblog.dto;

import lombok.Data;

@Data
public class AnalyticsDto {
    private long todayPv;
    private long todayUv;
    private long yesterdayPv;
    private long yesterdayUv;
    private long weekPv;
    private long lastWeekPv;
    private long monthPv;
    private long lastMonthPv;
}
