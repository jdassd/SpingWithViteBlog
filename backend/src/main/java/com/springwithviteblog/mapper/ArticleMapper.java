package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.ArticleStatus;
import com.springwithviteblog.domain.ContentType;
import com.springwithviteblog.domain.Visibility;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper {
  @Insert("INSERT INTO articles (author_id, title, content_type, content_raw, content_html, summary, cover_url, status, visibility, rss_enabled, allow_index, series_id, series_order, scheduled_at, created_at, updated_at, published_at) "
      + "VALUES (#{authorId}, #{title}, #{contentType}, #{contentRaw}, #{contentHtml}, #{summary}, #{coverUrl}, #{status}, #{visibility}, #{rssEnabled}, #{allowIndex}, #{seriesId}, #{seriesOrder}, #{scheduledAt}, #{createdAt}, #{updatedAt}, #{publishedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Article article);

  @Update("UPDATE articles SET title = #{title}, content_type = #{contentType}, content_raw = #{contentRaw}, content_html = #{contentHtml}, summary = #{summary}, cover_url = #{coverUrl}, status = #{status}, visibility = #{visibility}, rss_enabled = #{rssEnabled}, allow_index = #{allowIndex}, series_id = #{seriesId}, series_order = #{seriesOrder}, scheduled_at = #{scheduledAt}, updated_at = #{updatedAt}, published_at = #{publishedAt} WHERE id = #{id}")
  int update(Article article);

  @Update("UPDATE articles SET status = #{status}, published_at = #{publishedAt}, updated_at = #{updatedAt} WHERE id = #{id}")
  int updateStatus(@Param("id") Long id,
      @Param("status") ArticleStatus status,
      @Param("publishedAt") LocalDateTime publishedAt,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Update("UPDATE articles SET visibility = #{visibility}, updated_at = #{updatedAt} WHERE id = #{id}")
  int updateVisibility(@Param("id") Long id,
      @Param("visibility") Visibility visibility,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Select("SELECT * FROM articles WHERE id = #{id}")
  Article findById(@Param("id") Long id);

  @Select({
      "<script>",
      "SELECT * FROM articles",
      "<where>",
      "  <if test=\"keyword != null and keyword != ''\">",
      "    title LIKE CONCAT('%', #{keyword}, '%')",
      "  </if>",
      "  <if test='status != null'>",
      "    AND status = #{status}",
      "  </if>",
      "  <if test='contentType != null'>",
      "    AND content_type = #{contentType}",
      "  </if>",
      "  <if test='visibility != null'>",
      "    AND visibility = #{visibility}",
      "  </if>",
      "  <if test='authorId != null'>",
      "    AND author_id = #{authorId}",
      "  </if>",
      "</where>",
      "ORDER BY created_at DESC",
      "LIMIT #{limit} OFFSET #{offset}",
      "</script>"
  })
  List<Article> search(@Param("keyword") String keyword,
      @Param("status") ArticleStatus status,
      @Param("contentType") ContentType contentType,
      @Param("visibility") Visibility visibility,
      @Param("authorId") Long authorId,
      @Param("offset") int offset,
      @Param("limit") int limit);

  @Select("SELECT * FROM articles WHERE status = 'PUBLISHED' AND visibility = 'PUBLIC' ORDER BY published_at DESC")
  List<Article> findPublishedPublic();

  @Select("SELECT * FROM articles WHERE status = 'PUBLISHED' AND visibility = 'PUBLIC' AND allow_index = TRUE ORDER BY published_at DESC")
  List<Article> findPublicPublished();

  @Select("SELECT * FROM articles WHERE status = 'PUBLISHED' ORDER BY published_at DESC")
  List<Article> findPublished();

  @Select("SELECT COUNT(1) FROM articles")
  int countAll();

  @Select("SELECT COUNT(1) FROM articles WHERE status = #{status}")
  int countByStatus(@Param("status") ArticleStatus status);

  @Delete("DELETE FROM articles WHERE id = #{id}")
  int delete(@Param("id") Long id);

  // V2.0 新增方法
  @Select("SELECT * FROM articles WHERE series_id = #{seriesId} ORDER BY series_order ASC, published_at ASC")
  List<Article> findBySeriesId(@Param("seriesId") Long seriesId);

  @Update("UPDATE articles SET series_id = NULL, series_order = 0 WHERE series_id = #{seriesId}")
  int clearSeriesArticles(@Param("seriesId") Long seriesId);

  @Update("UPDATE articles SET series_id = #{seriesId}, series_order = #{seriesOrder} WHERE id = #{id}")
  int updateSeriesInfo(@Param("id") Long id, @Param("seriesId") Long seriesId,
      @Param("seriesOrder") Integer seriesOrder);

  @Select("SELECT * FROM articles WHERE status = #{status} AND scheduled_at <= #{before}")
  List<Article> findByStatusAndScheduledBefore(@Param("status") String status, @Param("before") LocalDateTime before);
}
