package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.ArticleStats;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleStatsMapper {

    @Insert("INSERT INTO article_stats (article_id, view_count, like_count, favorite_count, comment_count) VALUES (#{articleId}, #{viewCount}, #{likeCount}, #{favoriteCount}, #{commentCount}) ON DUPLICATE KEY UPDATE view_count = #{viewCount}, like_count = #{likeCount}, favorite_count = #{favoriteCount}, comment_count = #{commentCount}, updated_at = CURRENT_TIMESTAMP")
    int upsert(ArticleStats stats);

    @Select("SELECT * FROM article_stats WHERE article_id = #{articleId}")
    ArticleStats findByArticle(@Param("articleId") Long articleId);

    @Update("UPDATE article_stats SET view_count = view_count + #{delta}, updated_at = CURRENT_TIMESTAMP WHERE article_id = #{articleId}")
    int incrementViewCount(@Param("articleId") Long articleId, @Param("delta") long delta);

    @Update("UPDATE article_stats SET like_count = like_count + #{delta}, updated_at = CURRENT_TIMESTAMP WHERE article_id = #{articleId}")
    int incrementLikeCount(@Param("articleId") Long articleId, @Param("delta") long delta);

    @Update("UPDATE article_stats SET favorite_count = favorite_count + #{delta}, updated_at = CURRENT_TIMESTAMP WHERE article_id = #{articleId}")
    int incrementFavoriteCount(@Param("articleId") Long articleId, @Param("delta") long delta);

    @Update("UPDATE article_stats SET comment_count = comment_count + #{delta}, updated_at = CURRENT_TIMESTAMP WHERE article_id = #{articleId}")
    int incrementCommentCount(@Param("articleId") Long articleId, @Param("delta") long delta);

    @Select("SELECT s.*, (s.view_count * #{wv} + s.like_count * #{wl} + s.comment_count * #{wc} + s.favorite_count * #{wf}) as score FROM article_stats s JOIN articles a ON s.article_id = a.id WHERE a.status = 'PUBLISHED' AND a.visibility = 'PUBLIC' ORDER BY score DESC LIMIT #{limit}")
    List<ArticleStats> getTopByScore(@Param("wv") int wv, @Param("wl") int wl, @Param("wc") int wc, @Param("wf") int wf,
            @Param("limit") int limit);

    @Insert("INSERT INTO article_stats (article_id, view_count, like_count, favorite_count, comment_count) VALUES (#{articleId}, 0, 0, 0, 0)")
    int initStats(@Param("articleId") Long articleId);
}
