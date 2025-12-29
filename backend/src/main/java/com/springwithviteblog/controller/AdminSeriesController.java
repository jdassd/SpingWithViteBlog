package com.springwithviteblog.controller;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.Series;
import com.springwithviteblog.dto.SeriesDto;
import com.springwithviteblog.mapper.ArticleMapper;
import com.springwithviteblog.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/series")
@RequiredArgsConstructor
public class AdminSeriesController {

    private final SeriesService seriesService;
    private final ArticleMapper articleMapper;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Series> seriesList = seriesService.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Series s : seriesList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", s.getId());
            item.put("title", s.getTitle());
            item.put("description", s.getDescription());
            item.put("coverUrl", s.getCoverUrl());
            item.put("status", s.getStatus().name());
            item.put("visibility", s.getVisibility().name());
            item.put("sortOrder", s.getSortOrder());
            item.put("articleCount", seriesService.getArticleCount(s.getId()));
            item.put("createdAt", s.getCreatedAt());
            item.put("updatedAt", s.getUpdatedAt());
            result.add(item);
        }
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Series series = seriesService.findById(id);
        if (series == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", series.getId());
        result.put("title", series.getTitle());
        result.put("description", series.getDescription());
        result.put("coverUrl", series.getCoverUrl());
        result.put("status", series.getStatus().name());
        result.put("visibility", series.getVisibility().name());
        result.put("sortOrder", series.getSortOrder());
        result.put("whitelistUserIds", seriesService.getWhitelistUserIds(id));
        result.put("createdAt", series.getCreatedAt());
        result.put("updatedAt", series.getUpdatedAt());
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @GetMapping("/{id}/articles")
    public ResponseEntity<?> getSeriesArticles(@PathVariable Long id) {
        List<Article> articles = articleMapper.findBySeriesId(id);
        return ResponseEntity.ok(Map.of("success", true, "data", articles));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SeriesDto dto) {
        Series series = seriesService.create(dto);
        return ResponseEntity.ok(Map.of("success", true, "data", series));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SeriesDto dto) {
        Series series = seriesService.update(id, dto);
        if (series == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("success", true, "data", series));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean success = seriesService.delete(id);
        return ResponseEntity.ok(Map.of("success", success));
    }

    @PutMapping("/{id}/articles")
    public ResponseEntity<?> updateSeriesArticles(@PathVariable Long id, @RequestBody List<Long> articleIds) {
        // Remove articles from this series
        articleMapper.clearSeriesArticles(id);
        // Add articles to series with order
        for (int i = 0; i < articleIds.size(); i++) {
            articleMapper.updateSeriesInfo(articleIds.get(i), id, i);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
