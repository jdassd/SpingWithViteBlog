package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.SearchEngine;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SearchEngineMapper {
  @Insert("INSERT INTO search_engines (name, query_url, enabled, is_default, created_at, updated_at) "
      + "VALUES (#{name}, #{queryUrl}, #{enabled}, #{isDefault}, #{createdAt}, #{updatedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(SearchEngine engine);

  @Update("UPDATE search_engines SET name = #{name}, query_url = #{queryUrl}, enabled = #{enabled}, is_default = #{isDefault}, updated_at = #{updatedAt} WHERE id = #{id}")
  int update(SearchEngine engine);

  @Select("SELECT * FROM search_engines ORDER BY created_at DESC")
  List<SearchEngine> listAll();

  @Select("SELECT * FROM search_engines WHERE enabled = TRUE ORDER BY created_at DESC")
  List<SearchEngine> listEnabled();

  @Select("SELECT * FROM search_engines WHERE id = #{id}")
  SearchEngine findById(@Param("id") Long id);

  @Update("UPDATE search_engines SET is_default = FALSE")
  int clearDefault();

  @Delete("DELETE FROM search_engines WHERE id = #{id}")
  int delete(@Param("id") Long id);
}
