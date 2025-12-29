package com.springwithviteblog.service;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.ArticleStats;
import com.springwithviteblog.dto.RankingDto;
import com.springwithviteblog.mapper.ArticleMapper;
import com.springwithviteblog.mapper.ArticleStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final ArticleStatsMapper statsMapper;
    private final ArticleMapper articleMapper;
    private final SiteSettingService settingService;

    public List<RankingDto> getTopArticles(int limit) {
        int wv = getWeight("ranking.weight.views", 1);
        int wl = getWeight("ranking.weight.likes", 3);
        int wc = getWeight("ranking.weight.comments", 5);
        int wf = getWeight("ranking.weight.favorites", 2);

        List<ArticleStats> statsList = statsMapper.getTopByScore(wv, wl, wc, wf, limit);
        List<RankingDto> result = new ArrayList<>();

        for (ArticleStats stats : statsList) {
            Article article = articleMapper.findById(stats.getArticleId());
            if (article != null) {
                RankingDto dto = new RankingDto();
                dto.setArticleId(article.getId());
                dto.setTitle(article.getTitle());
                dto.setCoverUrl(article.getCoverUrl());
                dto.setAuthorId(article.getAuthorId());
                dto.setViewCount(stats.getViewCount());
                dto.setLikeCount(stats.getLikeCount());
                dto.setCommentCount(stats.getCommentCount());
                dto.setFavoriteCount(stats.getFavoriteCount());
                dto.setScore(stats.getViewCount() * wv + stats.getLikeCount() * wl + stats.getCommentCount() * wc
                        + stats.getFavoriteCount() * wf);
                result.add(dto);
            }
        }
        return result;
    }

    private int getWeight(String key, int defaultValue) {
        String value = settingService.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public void updateWeights(int views, int likes, int comments, int favorites) {
        settingService.set("ranking.weight.views", String.valueOf(views));
        settingService.set("ranking.weight.likes", String.valueOf(likes));
        settingService.set("ranking.weight.comments", String.valueOf(comments));
        settingService.set("ranking.weight.favorites", String.valueOf(favorites));
    }
}
