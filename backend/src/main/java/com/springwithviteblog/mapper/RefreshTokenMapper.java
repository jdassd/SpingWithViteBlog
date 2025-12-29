package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.RefreshToken;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RefreshTokenMapper {
  @Insert("INSERT INTO refresh_tokens (user_id, token_hash, expires_at, revoked_at, created_at) "
      + "VALUES (#{userId}, #{tokenHash}, #{expiresAt}, #{revokedAt}, #{createdAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(RefreshToken token);

  @Select("SELECT * FROM refresh_tokens WHERE token_hash = #{tokenHash}")
  RefreshToken findByHash(@Param("tokenHash") String tokenHash);

  @Update("UPDATE refresh_tokens SET revoked_at = #{revokedAt} WHERE id = #{id}")
  int revoke(@Param("id") Long id, @Param("revokedAt") LocalDateTime revokedAt);

  @Update("UPDATE refresh_tokens SET revoked_at = #{revokedAt} WHERE token_hash = #{tokenHash}")
  int revokeByHash(@Param("tokenHash") String tokenHash, @Param("revokedAt") LocalDateTime revokedAt);
}
