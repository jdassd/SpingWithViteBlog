package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Tag;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TagMapper {
  @Select("SELECT * FROM tags WHERE name = #{name}")
  Tag findByName(@Param("name") String name);

  @Select("SELECT * FROM tags WHERE slug = #{slug}")
  Tag findBySlug(@Param("slug") String slug);

  @Insert("INSERT INTO tags (name, slug) VALUES (#{name}, #{slug})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Tag tag);

  @Select("SELECT t.* FROM tags t JOIN article_tags at ON t.id = at.tag_id WHERE at.article_id = #{articleId}")
  List<Tag> findByArticleId(@Param("articleId") Long articleId);

  @Delete("DELETE FROM article_tags WHERE article_id = #{articleId}")
  int deleteArticleTags(@Param("articleId") Long articleId);

  @Insert("INSERT INTO article_tags (article_id, tag_id) VALUES (#{articleId}, #{tagId})")
  int insertArticleTag(@Param("articleId") Long articleId, @Param("tagId") Long tagId);

  @Select("SELECT * FROM tags ORDER BY name")
  List<Tag> listAll();
}
