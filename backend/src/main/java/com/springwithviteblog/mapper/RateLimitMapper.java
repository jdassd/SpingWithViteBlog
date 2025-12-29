package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.RateLimitRecord;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RateLimitMapper {
  @Select("SELECT * FROM rate_limits WHERE rate_key = #{rateKey}")
  RateLimitRecord findByKey(@Param("rateKey") String rateKey);

  @Insert("INSERT INTO rate_limits (rate_key, window_start, count) VALUES (#{rateKey}, #{windowStart}, #{count})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(RateLimitRecord record);

  @Update("UPDATE rate_limits SET window_start = #{windowStart}, count = #{count} WHERE id = #{id}")
  int update(@Param("id") Long id,
             @Param("windowStart") LocalDateTime windowStart,
             @Param("count") int count);
}
