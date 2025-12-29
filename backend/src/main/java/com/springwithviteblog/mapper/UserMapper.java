package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Role;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.domain.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
  @Select("SELECT * FROM users WHERE id = #{id}")
  User findById(@Param("id") Long id);

  @Select("SELECT * FROM users WHERE username = #{username}")
  User findByUsername(@Param("username") String username);

  @Select("SELECT * FROM users WHERE email = #{email}")
  User findByEmail(@Param("email") String email);

  @Select({
      "<script>",
      "SELECT * FROM users",
      "<where>",
      "  <if test=\"keyword != null and keyword != ''\">",
      "    (username LIKE CONCAT('%', #{keyword}, '%')",
      "     OR email LIKE CONCAT('%', #{keyword}, '%'))",
      "  </if>",
      "  <if test='role != null'>",
      "    AND role = #{role}",
      "  </if>",
      "  <if test='status != null'>",
      "    AND status = #{status}",
      "  </if>",
      "</where>",
      "ORDER BY created_at DESC",
      "</script>"
  })
  List<User> search(@Param("keyword") String keyword,
                    @Param("role") Role role,
                    @Param("status") UserStatus status);

  @Select("SELECT COUNT(1) FROM users")
  int countAll();

  @Insert("INSERT INTO users (username, email, password_hash, role, status, is_default_admin, failed_login_count, locked_until, created_at, updated_at) "
      + "VALUES (#{username}, #{email}, #{passwordHash}, #{role}, #{status}, #{isDefaultAdmin}, #{failedLoginCount}, #{lockedUntil}, #{createdAt}, #{updatedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(User user);

  @Update("UPDATE users SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
  int updateStatus(@Param("id") Long id, @Param("status") UserStatus status, @Param("updatedAt") LocalDateTime updatedAt);

  @Update("UPDATE users SET role = #{role}, updated_at = #{updatedAt} WHERE id = #{id}")
  int updateRole(@Param("id") Long id, @Param("role") Role role, @Param("updatedAt") LocalDateTime updatedAt);

  @Update("UPDATE users SET password_hash = #{passwordHash}, updated_at = #{updatedAt} WHERE id = #{id}")
  int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash, @Param("updatedAt") LocalDateTime updatedAt);

  @Update("UPDATE users SET failed_login_count = #{failedLoginCount}, locked_until = #{lockedUntil}, updated_at = #{updatedAt} WHERE id = #{id}")
  int updateFailedLogin(@Param("id") Long id,
                        @Param("failedLoginCount") int failedLoginCount,
                        @Param("lockedUntil") LocalDateTime lockedUntil,
                        @Param("updatedAt") LocalDateTime updatedAt);

  @Update("UPDATE users SET failed_login_count = 0, locked_until = NULL, updated_at = #{updatedAt}, last_login_at = #{lastLoginAt} WHERE id = #{id}")
  int updateLoginSuccess(@Param("id") Long id,
                         @Param("updatedAt") LocalDateTime updatedAt,
                         @Param("lastLoginAt") LocalDateTime lastLoginAt);

  @Delete("DELETE FROM users WHERE id = #{id}")
  int deleteById(@Param("id") Long id);
}
