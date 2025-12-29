package com.springwithviteblog.controller;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.Role;
import com.springwithviteblog.domain.Series;
import com.springwithviteblog.mapper.ArticleMapper;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class PublicSeriesController {

    private final SeriesService seriesService;
    private final ArticleMapper articleMapper;

    @GetMapping
    public ResponseEntity<?> getAll(@AuthenticationPrincipal SecurityUser user) {
        List<Series> all = seriesService.findAll();
        boolean isAdmin = user != null && user.getRole() == Role.ADMIN;
        List<Map<String, Object>> result = new ArrayList<>();
        for (Series s : all) {
            if (seriesService.canUserAccessSeries(s, user != null ? user.getId() : null, isAdmin)) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", s.getId());
                item.put("title", s.getTitle());
                item.put("description", s.getDescription());
                item.put("coverUrl", s.getCoverUrl());
                item.put("status", s.getStatus().name());
                item.put("articleCount", seriesService.getArticleCount(s.getId()));
                result.add(item);
            }
        }
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, @AuthenticationPrincipal SecurityUser user) {
        Series series = seriesService.findById(id);
        if (series == null) {
            return ResponseEntity.notFound().build();
        }
        boolean isAdmin = user != null && user.getRole() == Role.ADMIN;
        if (!seriesService.canUserAccessSeries(series, user != null ? user.getId() : null, isAdmin)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", Map.of("message", "Forbidden")));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", series.getId());
        result.put("title", series.getTitle());
        result.put("description", series.getDescription());
        result.put("coverUrl", series.getCoverUrl());
        result.put("status", series.getStatus().name());
        result.put("articleCount", seriesService.getArticleCount(id));

        // Get articles in series
        List<Article> articles = articleMapper.findBySeriesId(id);
        List<Map<String, Object>> articleList = new ArrayList<>();
        for (Article a : articles) {
            Map<String, Object> ai = new HashMap<>();
            ai.put("id", a.getId());
            ai.put("title", a.getTitle());
            ai.put("summary", a.getSummary());
            ai.put("coverUrl", a.getCoverUrl());
            ai.put("seriesOrder", a.getSeriesOrder());
            ai.put("publishedAt", a.getPublishedAt());
            articleList.add(ai);
        }
        result.put("articles", articleList);

        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }
}
