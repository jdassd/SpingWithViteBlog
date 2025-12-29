package com.springwithviteblog.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SeriesWhitelistMapper {

    @Insert("INSERT INTO series_whitelist (series_id, user_id) VALUES (#{seriesId}, #{userId})")
    int insert(@Param("seriesId") Long seriesId, @Param("userId") Long userId);

    @Delete("DELETE FROM series_whitelist WHERE series_id = #{seriesId}")
    int deleteBySeriesId(@Param("seriesId") Long seriesId);

    @Select("SELECT user_id FROM series_whitelist WHERE series_id = #{seriesId}")
    List<Long> findUserIdsBySeriesId(@Param("seriesId") Long seriesId);

    @Select("SELECT COUNT(*) FROM series_whitelist WHERE series_id = #{seriesId} AND user_id = #{userId}")
    int existsBySeriesAndUser(@Param("seriesId") Long seriesId, @Param("userId") Long userId);
}
