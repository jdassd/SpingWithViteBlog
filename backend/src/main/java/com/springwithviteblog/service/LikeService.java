package com.springwithviteblog.service;

import com.springwithviteblog.domain.ArticleLike;
import com.springwithviteblog.mapper.ArticleLikeMapper;
import com.springwithviteblog.mapper.ArticleStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final ArticleLikeMapper likeMapper;
    private final ArticleStatsMapper statsMapper;

    @Transactional
    public boolean likeArticle(Long articleId, Long userId) {
        ArticleLike existing = likeMapper.findByArticleAndUser(articleId, userId);
        if (existing != null) {
            return false; // Already liked
        }
        ArticleLike like = new ArticleLike();
        like.setArticleId(articleId);
        like.setUserId(userId);
        likeMapper.insert(like);
        statsMapper.incrementLikeCount(articleId, 1);
        return true;
    }

    @Transactional
    public boolean unlikeArticle(Long articleId, Long userId) {
        ArticleLike existing = likeMapper.findByArticleAndUser(articleId, userId);
        if (existing == null) {
            return false; // Not liked
        }
        likeMapper.delete(articleId, userId);
        statsMapper.incrementLikeCount(articleId, -1);
        return true;
    }

    public boolean isLiked(Long articleId, Long userId) {
        if (userId == null)
            return false;
        return likeMapper.findByArticleAndUser(articleId, userId) != null;
    }

    public long getLikeCount(Long articleId) {
        return likeMapper.countByArticle(articleId);
    }

    public List<Long> getLikedArticleIds(Long userId) {
        return likeMapper.findArticleIdsByUser(userId);
    }
}
