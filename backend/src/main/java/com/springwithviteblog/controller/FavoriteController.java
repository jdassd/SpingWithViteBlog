package com.springwithviteblog.controller;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.Favorite;
import com.springwithviteblog.domain.FavoriteArticle;
import com.springwithviteblog.dto.FavoriteDto;
import com.springwithviteblog.mapper.ArticleMapper;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final ArticleMapper articleMapper;

    @GetMapping
    public ResponseEntity<?> getMyFavorites(@AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "error", Map.of("message", "Please login")));
        }
        favoriteService.getOrCreateDefaultFavorite(user.getId());
        List<Favorite> favorites = favoriteService.getUserFavorites(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "data", favorites));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPublicFavorites(@PathVariable Long userId) {
        List<Favorite> favorites = favoriteService.getPublicFavorites(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", favorites));
    }

    @PostMapping
    public ResponseEntity<?> createFavorite(@RequestBody FavoriteDto dto, @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "error", Map.of("message", "Please login")));
        }
        Favorite favorite = favoriteService.createFavorite(user.getId(), dto);
        return ResponseEntity.ok(Map.of("success", true, "data", favorite));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFavorite(@PathVariable Long id, @RequestBody FavoriteDto dto,
            @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "error", Map.of("message", "Please login")));
        }
        Favorite existing = favoriteService.getFavoriteById(id);
        if (existing == null || !existing.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", Map.of("message", "Forbidden")));
        }
        Favorite favorite = favoriteService.updateFavorite(id, dto);
        return ResponseEntity.ok(Map.of("success", true, "data", favorite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavorite(@PathVariable Long id, @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "error", Map.of("message", "Please login")));
        }
        boolean success = favoriteService.deleteFavorite(id, user.getId());
        return ResponseEntity.ok(Map.of("success", success));
    }

    @GetMapping("/{id}/articles")
    public ResponseEntity<?> getFavoriteArticles(@PathVariable Long id, @AuthenticationPrincipal SecurityUser user) {
        Favorite favorite = favoriteService.getFavoriteById(id);
        if (favorite == null) {
            return ResponseEntity.notFound().build();
        }
        // Check access
        if (!Boolean.TRUE.equals(favorite.getIsPublic())
                && (user == null || !favorite.getUserId().equals(user.getId()))) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", Map.of("message", "Forbidden")));
        }
        List<FavoriteArticle> faList = favoriteService.getFavoriteArticles(id);
        List<Map<String, Object>> result = new ArrayList<>();
        for (FavoriteArticle fa : faList) {
            Article article = articleMapper.findById(fa.getArticleId());
            if (article != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", article.getId());
                item.put("title", article.getTitle());
                item.put("summary", article.getSummary());
                item.put("coverUrl", article.getCoverUrl());
                item.put("createdAt", article.getCreatedAt());
                item.put("favoritedAt", fa.getCreatedAt());
                result.add(item);
            }
        }
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @PostMapping("/{id}/articles/{articleId}")
    public ResponseEntity<?> addArticleToFavorite(@PathVariable Long id, @PathVariable Long articleId,
            @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "error", Map.of("message", "Please login")));
        }
        boolean success = favoriteService.addArticleToFavorite(id, articleId, user.getId());
        return ResponseEntity.ok(Map.of("success", success));
    }

    @DeleteMapping("/{id}/articles/{articleId}")
    public ResponseEntity<?> removeArticleFromFavorite(@PathVariable Long id, @PathVariable Long articleId,
            @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "error", Map.of("message", "Please login")));
        }
        boolean success = favoriteService.removeArticleFromFavorite(id, articleId, user.getId());
        return ResponseEntity.ok(Map.of("success", success));
    }

    @GetMapping("/article/{articleId}/status")
    public ResponseEntity<?> getArticleFavoriteStatus(@PathVariable Long articleId,
            @AuthenticationPrincipal SecurityUser user) {
        boolean isFavorited = favoriteService.isArticleFavorited(user != null ? user.getId() : null, articleId);
        long count = favoriteService.getFavoriteCount(articleId);
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("isFavorited", isFavorited, "count", count)));
    }
}
