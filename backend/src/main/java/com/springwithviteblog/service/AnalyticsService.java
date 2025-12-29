package com.springwithviteblog.service;

import com.springwithviteblog.domain.PageView;
import com.springwithviteblog.dto.AnalyticsDto;
import com.springwithviteblog.mapper.PageViewMapper;
import com.springwithviteblog.mapper.ArticleStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final PageViewMapper pageViewMapper;
    private final ArticleStatsMapper statsMapper;

    @Transactional
    public void recordPageView(Long articleId, String pagePath, String visitorIp, Long userId, String userAgent,
            String referer) {
        PageView pv = new PageView();
        pv.setArticleId(articleId);
        pv.setPagePath(pagePath);
        pv.setVisitorIp(visitorIp);
        pv.setUserId(userId);
        pv.setUserAgent(userAgent);
        pv.setReferer(referer);
        pageViewMapper.insert(pv);

        if (articleId != null) {
            // Try to increment, if no row exists, init first
            int updated = statsMapper.incrementViewCount(articleId, 1);
            if (updated == 0) {
                statsMapper.initStats(articleId);
                statsMapper.incrementViewCount(articleId, 1);
            }
        }
    }

    public AnalyticsDto getOverview() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        LocalDateTime yesterdayStart = today.minusDays(1).atStartOfDay();

        LocalDateTime weekStart = today.minusDays(7).atStartOfDay();
        LocalDateTime lastWeekStart = today.minusDays(14).atStartOfDay();

        LocalDateTime monthStart = today.minusDays(30).atStartOfDay();
        LocalDateTime lastMonthStart = today.minusDays(60).atStartOfDay();

        AnalyticsDto dto = new AnalyticsDto();
        dto.setTodayPv(pageViewMapper.countByDateRange(todayStart, todayEnd));
        dto.setTodayUv(pageViewMapper.countDistinctVisitorsByDateRange(todayStart, todayEnd));
        dto.setYesterdayPv(pageViewMapper.countByDateRange(yesterdayStart, todayStart));
        dto.setYesterdayUv(pageViewMapper.countDistinctVisitorsByDateRange(yesterdayStart, todayStart));

        dto.setWeekPv(pageViewMapper.countByDateRange(weekStart, todayEnd));
        dto.setLastWeekPv(pageViewMapper.countByDateRange(lastWeekStart, weekStart));

        dto.setMonthPv(pageViewMapper.countByDateRange(monthStart, todayEnd));
        dto.setLastMonthPv(pageViewMapper.countByDateRange(lastMonthStart, monthStart));

        return dto;
    }

    public List<Map<String, Object>> getDailyStats(int days) {
        LocalDateTime start = LocalDate.now().minusDays(days).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        return pageViewMapper.countByDay(start, end);
    }

    public List<Map<String, Object>> getTopArticles(int days, int limit) {
        LocalDateTime start = LocalDate.now().minusDays(days).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        return pageViewMapper.getTopArticles(start, end, limit);
    }
}
