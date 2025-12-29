package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.FavoriteArticle;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteArticleMapper {

    @Insert("INSERT INTO favorite_articles (favorite_id, article_id) VALUES (#{favoriteId}, #{articleId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FavoriteArticle fa);

    @Delete("DELETE FROM favorite_articles WHERE favorite_id = #{favoriteId} AND article_id = #{articleId}")
    int delete(@Param("favoriteId") Long favoriteId, @Param("articleId") Long articleId);

    @Delete("DELETE FROM favorite_articles WHERE favorite_id = #{favoriteId}")
    int deleteByFavorite(@Param("favoriteId") Long favoriteId);

    @Select("SELECT * FROM favorite_articles WHERE favorite_id = #{favoriteId} ORDER BY created_at DESC")
    List<FavoriteArticle> findByFavorite(@Param("favoriteId") Long favoriteId);

    @Select("SELECT * FROM favorite_articles WHERE favorite_id = #{favoriteId} AND article_id = #{articleId}")
    FavoriteArticle findByFavoriteAndArticle(@Param("favoriteId") Long favoriteId, @Param("articleId") Long articleId);

    @Select("SELECT COUNT(*) FROM favorite_articles WHERE article_id = #{articleId}")
    long countByArticle(@Param("articleId") Long articleId);

    @Select("SELECT fa.* FROM favorite_articles fa JOIN favorites f ON fa.favorite_id = f.id WHERE f.user_id = #{userId} AND fa.article_id = #{articleId}")
    List<FavoriteArticle> findByUserAndArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);
}
