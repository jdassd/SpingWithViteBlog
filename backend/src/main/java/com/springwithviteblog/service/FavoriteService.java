package com.springwithviteblog.service;

import com.springwithviteblog.domain.Favorite;
import com.springwithviteblog.domain.FavoriteArticle;
import com.springwithviteblog.dto.FavoriteDto;
import com.springwithviteblog.mapper.FavoriteArticleMapper;
import com.springwithviteblog.mapper.FavoriteMapper;
import com.springwithviteblog.mapper.ArticleStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final FavoriteArticleMapper favoriteArticleMapper;
    private final ArticleStatsMapper statsMapper;

    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteMapper.findByUser(userId);
    }

    public List<Favorite> getPublicFavorites(Long userId) {
        return favoriteMapper.findPublicByUser(userId);
    }

    public Favorite getFavoriteById(Long id) {
        return favoriteMapper.findById(id);
    }

    @Transactional
    public Favorite createFavorite(Long userId, FavoriteDto dto) {
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setName(dto.getName());
        favorite.setDescription(dto.getDescription());
        favorite.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : false);
        favorite.setIsDefault(false);
        favoriteMapper.insert(favorite);
        return favorite;
    }

    @Transactional
    public Favorite updateFavorite(Long id, FavoriteDto dto) {
        Favorite favorite = favoriteMapper.findById(id);
        if (favorite == null) {
            return null;
        }
        favorite.setName(dto.getName());
        favorite.setDescription(dto.getDescription());
        if (dto.getIsPublic() != null) {
            favorite.setIsPublic(dto.getIsPublic());
        }
        favoriteMapper.update(favorite);
        return favorite;
    }

    @Transactional
    public boolean deleteFavorite(Long id, Long userId) {
        Favorite favorite = favoriteMapper.findById(id);
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            return false;
        }
        if (Boolean.TRUE.equals(favorite.getIsDefault())) {
            return false; // Cannot delete default favorite
        }
        // Move articles to default favorite
        Favorite defaultFav = favoriteMapper.findDefaultByUser(userId);
        if (defaultFav != null) {
            List<FavoriteArticle> articles = favoriteArticleMapper.findByFavorite(id);
            for (FavoriteArticle fa : articles) {
                if (favoriteArticleMapper.findByFavoriteAndArticle(defaultFav.getId(), fa.getArticleId()) == null) {
                    FavoriteArticle newFa = new FavoriteArticle();
                    newFa.setFavoriteId(defaultFav.getId());
                    newFa.setArticleId(fa.getArticleId());
                    favoriteArticleMapper.insert(newFa);
                }
            }
        }
        favoriteArticleMapper.deleteByFavorite(id);
        favoriteMapper.delete(id);
        return true;
    }

    @Transactional
    public Favorite getOrCreateDefaultFavorite(Long userId) {
        Favorite defaultFav = favoriteMapper.findDefaultByUser(userId);
        if (defaultFav == null) {
            defaultFav = new Favorite();
            defaultFav.setUserId(userId);
            defaultFav.setName("默认收藏夹");
            defaultFav.setIsPublic(false);
            defaultFav.setIsDefault(true);
            favoriteMapper.insert(defaultFav);
        }
        return defaultFav;
    }

    @Transactional
    public boolean addArticleToFavorite(Long favoriteId, Long articleId, Long userId) {
        Favorite favorite = favoriteMapper.findById(favoriteId);
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            return false;
        }
        if (favoriteArticleMapper.findByFavoriteAndArticle(favoriteId, articleId) != null) {
            return false; // Already exists
        }
        FavoriteArticle fa = new FavoriteArticle();
        fa.setFavoriteId(favoriteId);
        fa.setArticleId(articleId);
        favoriteArticleMapper.insert(fa);
        statsMapper.incrementFavoriteCount(articleId, 1);
        return true;
    }

    @Transactional
    public boolean removeArticleFromFavorite(Long favoriteId, Long articleId, Long userId) {
        Favorite favorite = favoriteMapper.findById(favoriteId);
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            return false;
        }
        int deleted = favoriteArticleMapper.delete(favoriteId, articleId);
        if (deleted > 0) {
            statsMapper.incrementFavoriteCount(articleId, -1);
            return true;
        }
        return false;
    }

    public List<FavoriteArticle> getFavoriteArticles(Long favoriteId) {
        return favoriteArticleMapper.findByFavorite(favoriteId);
    }

    public boolean isArticleFavorited(Long userId, Long articleId) {
        if (userId == null)
            return false;
        List<FavoriteArticle> list = favoriteArticleMapper.findByUserAndArticle(userId, articleId);
        return !list.isEmpty();
    }

    public long getFavoriteCount(Long articleId) {
        return favoriteArticleMapper.countByArticle(articleId);
    }
}
