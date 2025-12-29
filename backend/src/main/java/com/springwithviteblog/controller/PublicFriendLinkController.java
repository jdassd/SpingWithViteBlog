package com.springwithviteblog.controller;

import com.springwithviteblog.domain.FriendLink;
import com.springwithviteblog.service.FriendLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friend-links")
@RequiredArgsConstructor
public class PublicFriendLinkController {

    private final FriendLinkService linkService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        Map<String, List<FriendLink>> grouped = linkService.findEnabledGroupedByCategory();
        return ResponseEntity.ok(Map.of("success", true, "data", grouped));
    }
}
