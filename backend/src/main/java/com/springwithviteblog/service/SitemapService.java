package com.springwithviteblog.service;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.Page;
import com.springwithviteblog.mapper.ArticleMapper;
import com.springwithviteblog.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SitemapService {

    private final ArticleMapper articleMapper;
    private final PageMapper pageMapper;
    private final SiteSettingService settingService;

    public String generateSitemap(String baseUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        String frequency = settingService.get("seo.sitemap.frequency");
        if (frequency == null)
            frequency = "daily";

        // Home page
        sb.append("  <url>\n");
        sb.append("    <loc>").append(baseUrl).append("/</loc>\n");
        sb.append("    <changefreq>").append(frequency).append("</changefreq>\n");
        sb.append("    <priority>1.0</priority>\n");
        sb.append("  </url>\n");

        // Articles
        List<Article> articles = articleMapper.findPublicPublished();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Article article : articles) {
            sb.append("  <url>\n");
            sb.append("    <loc>").append(baseUrl).append("/articles/").append(article.getId()).append("</loc>\n");
            if (article.getUpdatedAt() != null) {
                sb.append("    <lastmod>").append(article.getUpdatedAt().format(formatter)).append("</lastmod>\n");
            }
            sb.append("    <changefreq>weekly</changefreq>\n");
            sb.append("    <priority>0.8</priority>\n");
            sb.append("  </url>\n");
        }

        // Pages
        List<Page> pages = pageMapper.findPublicNav();
        for (Page page : pages) {
            if (page.getExternalUrl() == null || page.getExternalUrl().isEmpty()) {
                sb.append("  <url>\n");
                sb.append("    <loc>").append(baseUrl).append("/pages/").append(page.getSlug()).append("</loc>\n");
                if (page.getUpdatedAt() != null) {
                    sb.append("    <lastmod>").append(page.getUpdatedAt().format(formatter)).append("</lastmod>\n");
                }
                sb.append("    <changefreq>monthly</changefreq>\n");
                sb.append("    <priority>0.6</priority>\n");
                sb.append("  </url>\n");
            }
        }

        sb.append("</urlset>");
        return sb.toString();
    }

    public boolean isEnabled() {
        String enabled = settingService.get("seo.sitemap.enabled");
        return !"false".equalsIgnoreCase(enabled);
    }
}
