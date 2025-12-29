package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Insert("INSERT INTO favorites (user_id, name, description, is_public, is_default) VALUES (#{userId}, #{name}, #{description}, #{isPublic}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Favorite favorite);

    @Update("UPDATE favorites SET name = #{name}, description = #{description}, is_public = #{isPublic}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int update(Favorite favorite);

    @Delete("DELETE FROM favorites WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT * FROM favorites WHERE id = #{id}")
    Favorite findById(@Param("id") Long id);

    @Select("SELECT * FROM favorites WHERE user_id = #{userId} ORDER BY is_default DESC, created_at ASC")
    List<Favorite> findByUser(@Param("userId") Long userId);

    @Select("SELECT * FROM favorites WHERE user_id = #{userId} AND is_default = TRUE")
    Favorite findDefaultByUser(@Param("userId") Long userId);

    @Select("SELECT * FROM favorites WHERE user_id = #{userId} AND is_public = TRUE ORDER BY created_at ASC")
    List<Favorite> findPublicByUser(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId}")
    int countByUser(@Param("userId") Long userId);
}
