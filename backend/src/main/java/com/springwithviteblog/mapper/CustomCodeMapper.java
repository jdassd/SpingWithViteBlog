package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.CustomCodeType;
import com.springwithviteblog.domain.CustomCodeVersion;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CustomCodeMapper {
  @Insert("INSERT INTO custom_code_versions (type, content, enabled, created_by, created_at, is_active) "
      + "VALUES (#{type}, #{content}, #{enabled}, #{createdBy}, #{createdAt}, #{isActive})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(CustomCodeVersion version);

  @Select("SELECT * FROM custom_code_versions WHERE type = #{type} ORDER BY created_at DESC")
  List<CustomCodeVersion> listByType(@Param("type") CustomCodeType type);

  @Select("SELECT * FROM custom_code_versions WHERE type = #{type} AND is_active = TRUE")
  CustomCodeVersion findActive(@Param("type") CustomCodeType type);

  @Update("UPDATE custom_code_versions SET is_active = FALSE WHERE type = #{type}")
  int deactivateAll(@Param("type") CustomCodeType type);

  @Update("UPDATE custom_code_versions SET is_active = TRUE WHERE id = #{id}")
  int activate(@Param("id") Long id);
}
