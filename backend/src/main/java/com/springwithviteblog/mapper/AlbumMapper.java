package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Album;
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
public interface AlbumMapper {
  @Insert("INSERT INTO albums (title, description, cover_photo_id, owner_id, visibility, created_at, updated_at) "
      + "VALUES (#{title}, #{description}, #{coverPhotoId}, #{ownerId}, #{visibility}, #{createdAt}, #{updatedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Album album);

  @Update("UPDATE albums SET title = #{title}, description = #{description}, cover_photo_id = #{coverPhotoId}, visibility = #{visibility}, updated_at = #{updatedAt} WHERE id = #{id}")
  int update(Album album);

  @Select("SELECT * FROM albums WHERE id = #{id}")
  Album findById(@Param("id") Long id);

  @Select({
      "<script>",
      "SELECT * FROM albums",
      "<where>",
      "  <if test='ownerId != null'>",
      "    owner_id = #{ownerId}",
      "  </if>",
      "  <if test='visibility != null'>",
      "    AND visibility = #{visibility}",
      "  </if>",
      "</where>",
      "ORDER BY updated_at DESC",
      "</script>"
  })
  List<Album> list(@Param("ownerId") Long ownerId, @Param("visibility") Visibility visibility);

  @Select("SELECT COUNT(1) FROM albums")
  int countAll();

  @Delete("DELETE FROM albums WHERE id = #{id}")
  int delete(@Param("id") Long id);
}
