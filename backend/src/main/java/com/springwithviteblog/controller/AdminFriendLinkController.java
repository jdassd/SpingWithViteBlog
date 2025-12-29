package com.springwithviteblog.controller;

import com.springwithviteblog.domain.FriendLink;
import com.springwithviteblog.domain.LinkStatus;
import com.springwithviteblog.dto.FriendLinkDto;
import com.springwithviteblog.service.FriendLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/friend-links")
@RequiredArgsConstructor
public class AdminFriendLinkController {

    private final FriendLinkService linkService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<FriendLink> links = linkService.findAll();
        return ResponseEntity.ok(Map.of("success", true, "data", links));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        FriendLink link = linkService.findById(id);
        if (link == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("success", true, "data", link));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody FriendLinkDto dto) {
        FriendLink link = linkService.create(dto);
        return ResponseEntity.ok(Map.of("success", true, "data", link));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FriendLinkDto dto) {
        FriendLink link = linkService.update(id, dto);
        if (link == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("success", true, "data", link));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean success = linkService.delete(id);
        return ResponseEntity.ok(Map.of("success", success));
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<?> checkLink(@PathVariable Long id) {
        LinkStatus status = linkService.checkLink(id);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("status", status.name())));
    }

    @PostMapping("/check-all")
    public ResponseEntity<?> checkAllLinks() {
        linkService.checkAllLinks();
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<String> categories = linkService.getCategories();
        return ResponseEntity.ok(Map.of("success", true, "data", categories));
    }
}
