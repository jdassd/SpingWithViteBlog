package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.ArticleVersion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleVersionMapper {

    @Insert("INSERT INTO article_versions (article_id, version_number, title, content_raw, content_html) VALUES (#{articleId}, #{versionNumber}, #{title}, #{contentRaw}, #{contentHtml})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ArticleVersion version);

    @Select("SELECT * FROM article_versions WHERE article_id = #{articleId} ORDER BY version_number DESC")
    List<ArticleVersion> findByArticle(@Param("articleId") Long articleId);

    @Select("SELECT * FROM article_versions WHERE id = #{id}")
    ArticleVersion findById(@Param("id") Long id);

    @Select("SELECT COALESCE(MAX(version_number), 0) FROM article_versions WHERE article_id = #{articleId}")
    int getMaxVersionNumber(@Param("articleId") Long articleId);

    @Delete("DELETE FROM article_versions WHERE article_id = #{articleId} AND version_number < (SELECT MIN(vn) FROM (SELECT version_number AS vn FROM article_versions WHERE article_id = #{articleId} ORDER BY version_number DESC LIMIT #{keepCount}) AS keep_versions)")
    int deleteOldVersions(@Param("articleId") Long articleId, @Param("keepCount") int keepCount);
}
