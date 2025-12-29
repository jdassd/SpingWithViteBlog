package com.springwithviteblog.controller;

import com.springwithviteblog.dto.AnalyticsDto;
import com.springwithviteblog.dto.RankingConfigDto;
import com.springwithviteblog.service.AnalyticsService;
import com.springwithviteblog.service.RankingService;
import com.springwithviteblog.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;
    private final RankingService rankingService;
    private final SiteSettingService settingService;

    @GetMapping("/overview")
    public ResponseEntity<?> getOverview() {
        AnalyticsDto dto = analyticsService.getOverview();
        return ResponseEntity.ok(Map.of("success", true, "data", dto));
    }

    @GetMapping("/daily")
    public ResponseEntity<?> getDailyStats(@RequestParam(defaultValue = "7") int days) {
        List<Map<String, Object>> stats = analyticsService.getDailyStats(days);
        return ResponseEntity.ok(Map.of("success", true, "data", stats));
    }

    @GetMapping("/top-articles")
    public ResponseEntity<?> getTopArticles(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> articles = analyticsService.getTopArticles(days, limit);
        return ResponseEntity.ok(Map.of("success", true, "data", articles));
    }

    @GetMapping("/ranking-config")
    public ResponseEntity<?> getRankingConfig() {
        RankingConfigDto dto = new RankingConfigDto();
        dto.setViewWeight(getInt("ranking.weight.views", 1));
        dto.setLikeWeight(getInt("ranking.weight.likes", 3));
        dto.setCommentWeight(getInt("ranking.weight.comments", 5));
        dto.setFavoriteWeight(getInt("ranking.weight.favorites", 2));
        return ResponseEntity.ok(Map.of("success", true, "data", dto));
    }

    @PutMapping("/ranking-config")
    public ResponseEntity<?> updateRankingConfig(@RequestBody RankingConfigDto dto) {
        rankingService.updateWeights(
                dto.getViewWeight() != null ? dto.getViewWeight() : 1,
                dto.getLikeWeight() != null ? dto.getLikeWeight() : 3,
                dto.getCommentWeight() != null ? dto.getCommentWeight() : 5,
                dto.getFavoriteWeight() != null ? dto.getFavoriteWeight() : 2);
        return ResponseEntity.ok(Map.of("success", true));
    }

    private int getInt(String key, int defaultValue) {
        String value = settingService.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
