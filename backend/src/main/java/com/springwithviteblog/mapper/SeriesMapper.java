package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Series;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SeriesMapper {

    @Insert("INSERT INTO series (title, description, cover_url, status, visibility, sort_order) VALUES (#{title}, #{description}, #{coverUrl}, #{status}, #{visibility}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Series series);

    @Update("UPDATE series SET title = #{title}, description = #{description}, cover_url = #{coverUrl}, status = #{status}, visibility = #{visibility}, sort_order = #{sortOrder}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int update(Series series);

    @Delete("DELETE FROM series WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT * FROM series WHERE id = #{id}")
    Series findById(@Param("id") Long id);

    @Select("SELECT * FROM series ORDER BY sort_order ASC, created_at DESC")
    List<Series> findAll();

    @Select("SELECT * FROM series WHERE visibility = 'PUBLIC' ORDER BY sort_order ASC, created_at DESC")
    List<Series> findPublic();

    @Select("SELECT * FROM series WHERE status = #{status} ORDER BY sort_order ASC, created_at DESC")
    List<Series> findByStatus(@Param("status") String status);

    @Select("SELECT COUNT(*) FROM articles WHERE series_id = #{seriesId}")
    int countArticles(@Param("seriesId") Long seriesId);
}
