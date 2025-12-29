package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.PageView;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface PageViewMapper {

    @Insert("INSERT INTO page_views (article_id, page_path, visitor_ip, user_id, user_agent, referer) VALUES (#{articleId}, #{pagePath}, #{visitorIp}, #{userId}, #{userAgent}, #{referer})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PageView pageView);

    @Select("SELECT COUNT(*) FROM page_views WHERE article_id = #{articleId}")
    long countByArticle(@Param("articleId") Long articleId);

    @Select("SELECT COUNT(*) FROM page_views WHERE created_at >= #{start} AND created_at < #{end}")
    long countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT COUNT(*) FROM page_views WHERE article_id = #{articleId} AND created_at >= #{start} AND created_at < #{end}")
    long countByArticleAndDateRange(@Param("articleId") Long articleId, @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Select("SELECT COUNT(DISTINCT visitor_ip) FROM page_views WHERE created_at >= #{start} AND created_at < #{end}")
    long countDistinctVisitorsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT DATE(created_at) as date, COUNT(*) as count FROM page_views WHERE created_at >= #{start} AND created_at < #{end} GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> countByDay(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT article_id, COUNT(*) as count FROM page_views WHERE article_id IS NOT NULL AND created_at >= #{start} AND created_at < #{end} GROUP BY article_id ORDER BY count DESC LIMIT #{limit}")
    List<Map<String, Object>> getTopArticles(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
            @Param("limit") int limit);
}
