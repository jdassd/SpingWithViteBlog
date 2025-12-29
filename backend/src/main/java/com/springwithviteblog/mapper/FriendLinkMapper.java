package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.FriendLink;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendLinkMapper {

    @Insert("INSERT INTO friend_links (name, url, description, logo_url, category, sort_order, is_enabled) VALUES (#{name}, #{url}, #{description}, #{logoUrl}, #{category}, #{sortOrder}, #{isEnabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FriendLink link);

    @Update("UPDATE friend_links SET name = #{name}, url = #{url}, description = #{description}, logo_url = #{logoUrl}, category = #{category}, sort_order = #{sortOrder}, is_enabled = #{isEnabled}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int update(FriendLink link);

    @Update("UPDATE friend_links SET status = #{status}, last_check_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM friend_links WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT * FROM friend_links WHERE id = #{id}")
    FriendLink findById(@Param("id") Long id);

    @Select("SELECT * FROM friend_links ORDER BY sort_order ASC, created_at ASC")
    List<FriendLink> findAll();

    @Select("SELECT * FROM friend_links WHERE is_enabled = TRUE ORDER BY sort_order ASC, created_at ASC")
    List<FriendLink> findEnabled();

    @Select("SELECT DISTINCT category FROM friend_links WHERE category IS NOT NULL AND is_enabled = TRUE ORDER BY category")
    List<String> findCategories();

    @Select("SELECT * FROM friend_links WHERE is_enabled = TRUE AND category = #{category} ORDER BY sort_order ASC")
    List<FriendLink> findByCategory(@Param("category") String category);
}
