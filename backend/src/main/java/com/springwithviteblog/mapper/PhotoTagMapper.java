package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.PhotoTag;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PhotoTagMapper {
  @Select("SELECT * FROM photo_tags WHERE slug = #{slug}")
  PhotoTag findBySlug(@Param("slug") String slug);

  @Insert("INSERT INTO photo_tags (name, slug) VALUES (#{name}, #{slug})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(PhotoTag tag);

  @Select("SELECT t.* FROM photo_tags t JOIN photo_tag_relations r ON t.id = r.tag_id WHERE r.photo_id = #{photoId}")
  List<PhotoTag> findByPhotoId(@Param("photoId") Long photoId);

  @Delete("DELETE FROM photo_tag_relations WHERE photo_id = #{photoId}")
  int deleteByPhotoId(@Param("photoId") Long photoId);

  @Insert("INSERT INTO photo_tag_relations (photo_id, tag_id) VALUES (#{photoId}, #{tagId})")
  int insertRelation(@Param("photoId") Long photoId, @Param("tagId") Long tagId);
}
