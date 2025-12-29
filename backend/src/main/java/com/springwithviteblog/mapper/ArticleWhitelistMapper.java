package com.springwithviteblog.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleWhitelistMapper {
  @Insert("INSERT INTO article_whitelist (article_id, user_id) VALUES (#{articleId}, #{userId})")
  int insert(@Param("articleId") Long articleId, @Param("userId") Long userId);

  @Delete("DELETE FROM article_whitelist WHERE article_id = #{articleId}")
  int deleteByArticleId(@Param("articleId") Long articleId);

  @Select("SELECT user_id FROM article_whitelist WHERE article_id = #{articleId}")
  List<Long> findUserIds(@Param("articleId") Long articleId);

  @Select("SELECT COUNT(1) FROM article_whitelist WHERE article_id = #{articleId} AND user_id = #{userId}")
  int exists(@Param("articleId") Long articleId, @Param("userId") Long userId);
}
