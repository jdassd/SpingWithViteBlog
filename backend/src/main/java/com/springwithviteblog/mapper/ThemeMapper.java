package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Theme;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ThemeMapper {
  @Insert("INSERT INTO themes (name, version, author, description, storage_path, theme_json, is_active, config_json, created_at, updated_at) "
      + "VALUES (#{name}, #{version}, #{author}, #{description}, #{storagePath}, #{themeJson}, #{isActive}, #{configJson}, #{createdAt}, #{updatedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Theme theme);

  @Update("UPDATE themes SET name = #{name}, version = #{version}, author = #{author}, description = #{description}, storage_path = #{storagePath}, theme_json = #{themeJson}, is_active = #{isActive}, config_json = #{configJson}, updated_at = #{updatedAt} WHERE id = #{id}")
  int update(Theme theme);

  @Select("SELECT * FROM themes WHERE id = #{id}")
  Theme findById(@Param("id") Long id);

  @Select("SELECT * FROM themes WHERE is_active = TRUE")
  Theme findActive();

  @Select("SELECT * FROM themes ORDER BY created_at DESC")
  List<Theme> listAll();

  @Update("UPDATE themes SET is_active = FALSE")
  int clearActive();

  @Delete("DELETE FROM themes WHERE id = #{id}")
  int delete(@Param("id") Long id);
}
