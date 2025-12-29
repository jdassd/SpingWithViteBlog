package com.springwithviteblog.controller;

import com.springwithviteblog.service.SitemapService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SitemapController {

    private final SitemapService sitemapService;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemap(HttpServletRequest request) {
        if (!sitemapService.isEnabled()) {
            return ResponseEntity.notFound().build();
        }
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }
        String xml = sitemapService.generateSitemap(baseUrl);
        return ResponseEntity.ok(xml);
    }
}
