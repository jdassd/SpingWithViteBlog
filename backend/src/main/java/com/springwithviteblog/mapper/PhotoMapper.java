package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Photo;
import com.springwithviteblog.domain.SyncStatus;
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
public interface PhotoMapper {
  @Insert("INSERT INTO photos (album_id, filename, original_path, thumbnail_path, external_url, sync_status, sync_error, exif_json, taken_at, created_at, updated_at) "
      + "VALUES (#{albumId}, #{filename}, #{originalPath}, #{thumbnailPath}, #{externalUrl}, #{syncStatus}, #{syncError}, #{exifJson}, #{takenAt}, #{createdAt}, #{updatedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Photo photo);

  @Update("UPDATE photos SET thumbnail_path = #{thumbnailPath}, external_url = #{externalUrl}, sync_status = #{syncStatus}, sync_error = #{syncError}, exif_json = #{exifJson}, taken_at = #{takenAt}, updated_at = #{updatedAt} WHERE id = #{id}")
  int update(Photo photo);

  @Update("UPDATE photos SET sync_status = #{syncStatus}, sync_error = #{syncError}, updated_at = #{updatedAt} WHERE id = #{id}")
  int updateSyncStatus(@Param("id") Long id,
                       @Param("syncStatus") SyncStatus syncStatus,
                       @Param("syncError") String syncError,
                       @Param("updatedAt") LocalDateTime updatedAt);

  @Select("SELECT * FROM photos WHERE id = #{id}")
  Photo findById(@Param("id") Long id);

  @Select("SELECT * FROM photos WHERE album_id = #{albumId} ORDER BY created_at DESC")
  List<Photo> listByAlbum(@Param("albumId") Long albumId);

  @Select("SELECT COUNT(1) FROM photos")
  int countAll();

  @Delete("DELETE FROM photos WHERE id = #{id}")
  int delete(@Param("id") Long id);
}
