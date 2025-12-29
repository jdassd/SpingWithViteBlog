package com.springwithviteblog.controller;

import com.springwithviteblog.domain.User;
import com.springwithviteblog.dto.comment.CommentCreateRequest;
import com.springwithviteblog.dto.comment.CommentDto;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.CommentService;
import com.springwithviteblog.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicCommentController {
  private final CommentService commentService;
  private final UserService userService;

  public PublicCommentController(CommentService commentService, UserService userService) {
    this.commentService = commentService;
    this.userService = userService;
  }

  @GetMapping("/articles/{articleId}/comments")
  public ApiResponse<List<CommentDto>> list(@PathVariable Long articleId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.ok(commentService.listForArticle(articleId, currentUser(), page, size));
  }

  @PostMapping("/articles/{articleId}/comments")
  public ApiResponse<CommentDto> create(@PathVariable Long articleId,
                                        @Valid @RequestBody CommentCreateRequest request) {
    return ApiResponse.ok(commentService.create(articleId, currentUser(), request));
  }

  @DeleteMapping("/comments/{commentId}")
  public ApiResponse<Object> delete(@PathVariable Long commentId) {
    User user = currentUser();
    if (user == null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
    commentService.delete(commentId, user, false);
    return ApiResponse.ok(null);
  }

  private User currentUser() {
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser == null) {
      return null;
    }
    return userService.getById(securityUser.getId());
  }
}
