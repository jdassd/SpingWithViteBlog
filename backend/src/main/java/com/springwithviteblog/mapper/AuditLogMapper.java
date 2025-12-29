package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.AuditLog;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuditLogMapper {
  @Insert("INSERT INTO audit_logs (user_id, action, resource_type, resource_id, result, message, created_at) "
      + "VALUES (#{userId}, #{action}, #{resourceType}, #{resourceId}, #{result}, #{message}, #{createdAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(AuditLog log);

  @Select({
      "<script>",
      "SELECT * FROM audit_logs",
      "<where>",
      "  <if test='userId != null'>",
      "    user_id = #{userId}",
      "  </if>",
      "  <if test='action != null'>",
      "    AND action = #{action}",
      "  </if>",
      "</where>",
      "ORDER BY created_at DESC",
      "LIMIT #{limit} OFFSET #{offset}",
      "</script>"
  })
  List<AuditLog> list(@Param("userId") Long userId,
                      @Param("action") String action,
                      @Param("offset") int offset,
                      @Param("limit") int limit);
}
