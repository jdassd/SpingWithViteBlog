package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.NavigationGroup;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NavigationGroupMapper {
  @Insert("INSERT INTO navigation_groups (name, sort_order, created_at, updated_at) VALUES (#{name}, #{sortOrder}, #{createdAt}, #{updatedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(NavigationGroup group);

  @Update("UPDATE navigation_groups SET name = #{name}, sort_order = #{sortOrder}, updated_at = #{updatedAt} WHERE id = #{id}")
  int update(NavigationGroup group);

  @Select("SELECT * FROM navigation_groups ORDER BY sort_order ASC, created_at DESC")
  List<NavigationGroup> listAll();

  @Select("SELECT * FROM navigation_groups WHERE id = #{id}")
  NavigationGroup findById(@Param("id") Long id);

  @Delete("DELETE FROM navigation_groups WHERE id = #{id}")
  int delete(@Param("id") Long id);
}
