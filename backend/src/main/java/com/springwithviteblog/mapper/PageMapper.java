package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Page;
import com.springwithviteblog.domain.Visibility;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PageMapper {
  @Insert("INSERT INTO pages (title, slug, content_type, content_raw, content_html, visibility, is_nav, sort_order, external_url, created_at, updated_at) "
      + "VALUES (#{title}, #{slug}, #{contentType}, #{contentRaw}, #{contentHtml}, #{visibility}, #{isNav}, #{sortOrder}, #{externalUrl}, #{createdAt}, #{updatedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Page page);

  @Update("UPDATE pages SET title = #{title}, slug = #{slug}, content_type = #{contentType}, content_raw = #{contentRaw}, content_html = #{contentHtml}, visibility = #{visibility}, is_nav = #{isNav}, sort_order = #{sortOrder}, external_url = #{externalUrl}, updated_at = #{updatedAt} WHERE id = #{id}")
  int update(Page page);

  @Select("SELECT * FROM pages WHERE id = #{id}")
  Page findById(@Param("id") Long id);

  @Select("SELECT * FROM pages WHERE slug = #{slug}")
  Page findBySlug(@Param("slug") String slug);

  @Select({
      "<script>",
      "SELECT * FROM pages",
      "<where>",
      "  <if test=\"keyword != null and keyword != ''\">",
      "    title LIKE CONCAT('%', #{keyword}, '%')",
      "  </if>",
      "  <if test='visibility != null'>",
      "    AND visibility = #{visibility}",
      "  </if>",
      "  <if test='isNav != null'>",
      "    AND is_nav = #{isNav}",
      "  </if>",
      "</where>",
      "ORDER BY sort_order ASC, created_at DESC",
      "</script>"
  })
  List<Page> list(@Param("keyword") String keyword,
      @Param("visibility") Visibility visibility,
      @Param("isNav") Boolean isNav);

  @Select("SELECT * FROM pages WHERE is_nav = TRUE ORDER BY sort_order ASC, created_at DESC")
  List<Page> listNav();

  @Select("SELECT * FROM pages WHERE is_nav = TRUE AND visibility = 'PUBLIC' ORDER BY sort_order ASC, created_at DESC")
  List<Page> findPublicNav();

  @Delete("DELETE FROM pages WHERE id = #{id}")
  int delete(@Param("id") Long id);
}
