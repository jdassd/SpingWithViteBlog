package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Category;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper {
  @Select("SELECT * FROM categories WHERE name = #{name}")
  Category findByName(@Param("name") String name);

  @Select("SELECT * FROM categories WHERE slug = #{slug}")
  Category findBySlug(@Param("slug") String slug);

  @Insert("INSERT INTO categories (name, slug) VALUES (#{name}, #{slug})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Category category);

  @Select("SELECT c.* FROM categories c JOIN article_categories ac ON c.id = ac.category_id WHERE ac.article_id = #{articleId}")
  List<Category> findByArticleId(@Param("articleId") Long articleId);

  @Delete("DELETE FROM article_categories WHERE article_id = #{articleId}")
  int deleteArticleCategories(@Param("articleId") Long articleId);

  @Insert("INSERT INTO article_categories (article_id, category_id) VALUES (#{articleId}, #{categoryId})")
  int insertArticleCategory(@Param("articleId") Long articleId, @Param("categoryId") Long categoryId);

  @Select("SELECT * FROM categories ORDER BY name")
  List<Category> listAll();
}
