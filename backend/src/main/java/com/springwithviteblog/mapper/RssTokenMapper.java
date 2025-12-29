package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.RssToken;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RssTokenMapper {
  @Insert("INSERT INTO rss_tokens (user_id, token_hash, expires_at, revoked_at, created_at) "
      + "VALUES (#{userId}, #{tokenHash}, #{expiresAt}, #{revokedAt}, #{createdAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(RssToken token);

  @Select("SELECT * FROM rss_tokens WHERE token_hash = #{tokenHash}")
  RssToken findByHash(@Param("tokenHash") String tokenHash);

  @Update("UPDATE rss_tokens SET revoked_at = #{revokedAt} WHERE id = #{id}")
  int revoke(@Param("id") Long id, @Param("revokedAt") LocalDateTime revokedAt);
}
