package com.springwithviteblog.controller;

import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/articles/{id}/like")
    public ResponseEntity<?> likeArticle(@PathVariable Long id, @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "error", Map.of("message", "Please login to like")));
        }
        boolean success = likeService.likeArticle(id, user.getId());
        long count = likeService.getLikeCount(id);
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("liked", success, "count", count)));
    }

    @DeleteMapping("/articles/{id}/like")
    public ResponseEntity<?> unlikeArticle(@PathVariable Long id, @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "error", Map.of("message", "Please login")));
        }
        boolean success = likeService.unlikeArticle(id, user.getId());
        long count = likeService.getLikeCount(id);
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("unliked", success, "count", count)));
    }

    @GetMapping("/articles/{id}/like/status")
    public ResponseEntity<?> getLikeStatus(@PathVariable Long id, @AuthenticationPrincipal SecurityUser user) {
        boolean liked = likeService.isLiked(id, user != null ? user.getId() : null);
        long count = likeService.getLikeCount(id);
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("liked", liked, "count", count)));
    }
}
