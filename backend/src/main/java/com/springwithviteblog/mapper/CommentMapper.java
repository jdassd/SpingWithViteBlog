package com.springwithviteblog.mapper;

import com.springwithviteblog.domain.Comment;
import com.springwithviteblog.domain.CommentStatus;
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
public interface CommentMapper {
  @Insert("INSERT INTO comments (article_id, user_id, parent_id, guest_name, guest_email, content, status, created_at, updated_at) "
      + "VALUES (#{articleId}, #{userId}, #{parentId}, #{guestName}, #{guestEmail}, #{content}, #{status}, #{createdAt}, #{updatedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Comment comment);

  @Select("SELECT * FROM comments WHERE id = #{id}")
  Comment findById(@Param("id") Long id);

  @Select({
      "<script>",
      "SELECT * FROM comments",
      "<where>",
      "  <if test='articleId != null'>",
      "    article_id = #{articleId}",
      "  </if>",
      "  <if test='status != null'>",
      "    AND status = #{status}",
      "  </if>",
      "</where>",
      "ORDER BY created_at ASC",
      "LIMIT #{limit} OFFSET #{offset}",
      "</script>"
  })
  List<Comment> listByArticle(@Param("articleId") Long articleId,
                              @Param("status") CommentStatus status,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

  @Select({
      "<script>",
      "SELECT * FROM comments",
      "<where>",
      "  <if test='articleId != null'>",
      "    article_id = #{articleId}",
      "  </if>",
      "  <if test='status != null'>",
      "    AND status = #{status}",
      "  </if>",
      "</where>",
      "ORDER BY created_at DESC",
      "LIMIT #{limit} OFFSET #{offset}",
      "</script>"
  })
  List<Comment> listForAdmin(@Param("articleId") Long articleId,
                             @Param("status") CommentStatus status,
                             @Param("offset") int offset,
                             @Param("limit") int limit);

  @Update("UPDATE comments SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
  int updateStatus(@Param("id") Long id, @Param("status") CommentStatus status, @Param("updatedAt") LocalDateTime updatedAt);

  @Select("SELECT COUNT(1) FROM comments WHERE user_id = #{userId}")
  int countByUserId(@Param("userId") Long userId);

  @Select("SELECT COUNT(1) FROM comments WHERE guest_email = #{guestEmail}")
  int countByGuestEmail(@Param("guestEmail") String guestEmail);

  @Select("SELECT COUNT(1) FROM comments")
  int countAll();

  @Select("SELECT COUNT(1) FROM comments WHERE status = #{status}")
  int countByStatus(@Param("status") CommentStatus status);

  @Delete("DELETE FROM comments WHERE id = #{id}")
  int delete(@Param("id") Long id);
}
