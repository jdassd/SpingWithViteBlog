package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.ArticleLike;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleLikeMapper {

    @Insert("INSERT INTO article_likes (article_id, user_id) VALUES (#{articleId}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ArticleLike like);

    @Delete("DELETE FROM article_likes WHERE article_id = #{articleId} AND user_id = #{userId}")
    int delete(@Param("articleId") Long articleId, @Param("userId") Long userId);

    @Select("SELECT * FROM article_likes WHERE article_id = #{articleId} AND user_id = #{userId}")
    ArticleLike findByArticleAndUser(@Param("articleId") Long articleId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM article_likes WHERE article_id = #{articleId}")
    long countByArticle(@Param("articleId") Long articleId);

    @Select("SELECT article_id FROM article_likes WHERE user_id = #{userId}")
    List<Long> findArticleIdsByUser(@Param("userId") Long userId);
}
