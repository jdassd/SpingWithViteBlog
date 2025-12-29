package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.NavigationLink;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NavigationLinkMapper {
  @Insert("INSERT INTO navigation_links (group_id, name, url, icon, description, open_in_new, sort_order, created_at, updated_at) "
      + "VALUES (#{groupId}, #{name}, #{url}, #{icon}, #{description}, #{openInNew}, #{sortOrder}, #{createdAt}, #{updatedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(NavigationLink link);

  @Update("UPDATE navigation_links SET group_id = #{groupId}, name = #{name}, url = #{url}, icon = #{icon}, description = #{description}, open_in_new = #{openInNew}, sort_order = #{sortOrder}, updated_at = #{updatedAt} WHERE id = #{id}")
  int update(NavigationLink link);

  @Select("SELECT * FROM navigation_links WHERE group_id = #{groupId} ORDER BY sort_order ASC, created_at DESC")
  List<NavigationLink> listByGroup(@Param("groupId") Long groupId);

  @Select("SELECT * FROM navigation_links ORDER BY sort_order ASC, created_at DESC")
  List<NavigationLink> listAll();

  @Select("SELECT * FROM navigation_links WHERE id = #{id}")
  NavigationLink findById(@Param("id") Long id);

  @Delete("DELETE FROM navigation_links WHERE id = #{id}")
  int delete(@Param("id") Long id);
}
