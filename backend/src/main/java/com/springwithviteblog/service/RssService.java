package com.springwithviteblog.service;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedOutput;
import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.RssToken;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.RssTokenMapper;
import com.springwithviteblog.mapper.UserMapper;
import com.springwithviteblog.util.TokenHasher;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RssService {
  private final ArticleService articleService;
  private final SiteSettingService siteSettingService;
  private final RssTokenMapper rssTokenMapper;
  private final UserMapper userMapper;

  public RssService(ArticleService articleService,
                    SiteSettingService siteSettingService,
                    RssTokenMapper rssTokenMapper,
                    UserMapper userMapper) {
    this.articleService = articleService;
    this.siteSettingService = siteSettingService;
    this.rssTokenMapper = rssTokenMapper;
    this.userMapper = userMapper;
  }

  public String generatePublicFeed() {
    boolean enabled = siteSettingService.getBoolean("rss_public_enabled", true);
    if (!enabled) {
      throw new ApiException(ErrorCodes.OPERATION_NOT_ALLOWED, "Public RSS disabled", HttpStatus.FORBIDDEN);
    }
    List<Article> articles = articleService.listPublishedPublic();
    boolean fullContent = siteSettingService.getBoolean("rss_full_content", false);
    return buildFeed(articles, baseTitle("Public"), baseDescription(), baseLink(), fullContent);
  }

  public String generatePrivateFeed(String token) {
    boolean enabled = siteSettingService.getBoolean("rss_private_enabled", true);
    if (!enabled) {
      throw new ApiException(ErrorCodes.OPERATION_NOT_ALLOWED, "Private RSS disabled", HttpStatus.FORBIDDEN);
    }
    if (token == null || token.isBlank()) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Missing token", HttpStatus.UNAUTHORIZED);
    }
    RssToken rssToken = rssTokenMapper.findByHash(TokenHasher.sha256(token));
    if (rssToken == null || rssToken.getRevokedAt() != null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Token invalid", HttpStatus.UNAUTHORIZED);
    }
    if (rssToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new ApiException(ErrorCodes.TOKEN_EXPIRED, "Token expired", HttpStatus.UNAUTHORIZED);
    }
    User user = userMapper.findById(rssToken.getUserId());
    if (user == null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "User not found", HttpStatus.UNAUTHORIZED);
    }

    List<Article> articles = articleService.listPublishedForUser(user)
        .stream()
        .filter(Article::getRssEnabled)
        .toList();
    boolean fullContent = siteSettingService.getBoolean("rss_full_content", false);
    return buildFeed(articles, baseTitle("Private"), baseDescription(), baseLink(), fullContent);
  }

  public String createToken(User user, int days) {
    if (days <= 0) {
      String configuredDays = siteSettingService.get("rss_token_days");
      if (configuredDays != null && !configuredDays.isBlank()) {
        try {
          days = Integer.parseInt(configuredDays);
        } catch (NumberFormatException ex) {
          days = 30;
        }
      } else {
        days = 30;
      }
    }
    String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    RssToken rssToken = new RssToken();
    rssToken.setUserId(user.getId());
    rssToken.setTokenHash(TokenHasher.sha256(token));
    rssToken.setCreatedAt(LocalDateTime.now());
    rssToken.setExpiresAt(LocalDateTime.now().plusDays(days));
    rssTokenMapper.insert(rssToken);
    return token;
  }

  public void revokeToken(String token) {
    if (token == null || token.isBlank()) {
      return;
    }
    RssToken rssToken = rssTokenMapper.findByHash(TokenHasher.sha256(token));
    if (rssToken != null && rssToken.getRevokedAt() == null) {
      rssTokenMapper.revoke(rssToken.getId(), LocalDateTime.now());
    }
  }

  private String baseTitle(String type) {
    String siteName = siteSettingService.get("site_name");
    return (siteName == null ? "Blog" : siteName) + " " + type + " RSS";
  }

  private String baseDescription() {
    String description = siteSettingService.get("site_description");
    return description == null ? "Blog feed" : description;
  }

  private String baseLink() {
    String siteUrl = siteSettingService.get("site_url");
    return siteUrl == null ? "http://localhost:8080" : siteUrl;
  }

  private String buildFeed(List<Article> articles, String title, String description, String link, boolean fullContent) {
    SyndFeed feed = new SyndFeedImpl();
    feed.setFeedType("rss_2.0");
    feed.setTitle(title);
    feed.setDescription(description);
    feed.setLink(link);

    List<SyndEntry> entries = new ArrayList<>();
    for (Article article : articles) {
      if (!Boolean.TRUE.equals(article.getRssEnabled())) {
        continue;
      }
      SyndEntry entry = new SyndEntryImpl();
      entry.setTitle(article.getTitle());
      entry.setLink(link + "/articles/" + article.getId());
      if (article.getPublishedAt() != null) {
        entry.setPublishedDate(Date.from(article.getPublishedAt().atZone(ZoneId.systemDefault()).toInstant()));
      }
      SyndContent content = new SyndContentImpl();
      if (fullContent && article.getContentHtml() != null) {
        content.setType("text/html");
        content.setValue(article.getContentHtml());
      } else {
        content.setType("text/plain");
        content.setValue(article.getSummary());
      }
      entry.setDescription(content);
      entries.add(entry);
    }
    feed.setEntries(entries);
    try {
      return new SyndFeedOutput().outputString(feed);
    } catch (Exception ex) {
      throw new ApiException("RSS_ERROR", "Failed to build RSS", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
