package com.springwithviteblog.controller;

import com.springwithviteblog.dto.RankingDto;
import com.springwithviteblog.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<?> getRanking(@RequestParam(defaultValue = "20") int limit) {
        List<RankingDto> ranking = rankingService.getTopArticles(limit);
        return ResponseEntity.ok(Map.of("success", true, "data", ranking));
    }
}
