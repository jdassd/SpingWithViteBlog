package com.springwithviteblog.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PageWhitelistMapper {
  @Insert("INSERT INTO page_whitelist (page_id, user_id) VALUES (#{pageId}, #{userId})")
  int insert(@Param("pageId") Long pageId, @Param("userId") Long userId);

  @Delete("DELETE FROM page_whitelist WHERE page_id = #{pageId}")
  int deleteByPageId(@Param("pageId") Long pageId);

  @Select("SELECT COUNT(1) FROM page_whitelist WHERE page_id = #{pageId} AND user_id = #{userId}")
  int exists(@Param("pageId") Long pageId, @Param("userId") Long userId);

  @Select("SELECT user_id FROM page_whitelist WHERE page_id = #{pageId}")
  List<Long> findUserIds(@Param("pageId") Long pageId);
}
