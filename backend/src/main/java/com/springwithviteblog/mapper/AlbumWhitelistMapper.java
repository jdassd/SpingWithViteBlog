package com.springwithviteblog.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AlbumWhitelistMapper {
  @Insert("INSERT INTO album_whitelist (album_id, user_id) VALUES (#{albumId}, #{userId})")
  int insert(@Param("albumId") Long albumId, @Param("userId") Long userId);

  @Delete("DELETE FROM album_whitelist WHERE album_id = #{albumId}")
  int deleteByAlbumId(@Param("albumId") Long albumId);

  @Select("SELECT COUNT(1) FROM album_whitelist WHERE album_id = #{albumId} AND user_id = #{userId}")
  int exists(@Param("albumId") Long albumId, @Param("userId") Long userId);

  @Select("SELECT user_id FROM album_whitelist WHERE album_id = #{albumId}")
  List<Long> findUserIds(@Param("albumId") Long albumId);
}
