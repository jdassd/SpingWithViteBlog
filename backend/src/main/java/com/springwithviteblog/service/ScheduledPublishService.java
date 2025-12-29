package com.springwithviteblog.service;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.ArticleStatus;
import com.springwithviteblog.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledPublishService {

    private final ArticleMapper articleMapper;
    private final AuditService auditService;

    @Scheduled(fixedRate = 60000) // Every minute
    @Transactional
    public void publishScheduledArticles() {
        LocalDateTime now = LocalDateTime.now();
        List<Article> scheduledArticles = articleMapper.findByStatusAndScheduledBefore(
                ArticleStatus.SCHEDULED.name(), now);

        for (Article article : scheduledArticles) {
            try {
                article.setStatus(ArticleStatus.PUBLISHED);
                article.setPublishedAt(now);
                articleMapper.update(article);
                log.info("Published scheduled article: {} (ID: {})", article.getTitle(), article.getId());
                auditService.log(article.getAuthorId(), "SCHEDULED_PUBLISH", "ARTICLE", article.getId().toString(),
                        "SUCCESS", null);
            } catch (Exception e) {
                log.error("Failed to publish scheduled article {}: {}", article.getId(), e.getMessage());
                auditService.log(article.getAuthorId(), "SCHEDULED_PUBLISH", "ARTICLE", article.getId().toString(),
                        "FAILED", e.getMessage());
            }
        }
    }
}
