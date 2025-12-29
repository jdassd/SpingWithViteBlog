package com.springwithviteblog.controller;

import com.springwithviteblog.domain.CommentStatus;
import com.springwithviteblog.dto.comment.CommentDto;
import com.springwithviteblog.dto.comment.CommentStatusRequest;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.AuditService;
import com.springwithviteblog.service.CommentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/comments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {
  private final CommentService commentService;
  private final AuditService auditService;

  public AdminCommentController(CommentService commentService, AuditService auditService) {
    this.commentService = commentService;
    this.auditService = auditService;
  }

  @GetMapping
  public ApiResponse<List<CommentDto>> list(@RequestParam(required = false) Long articleId,
                                            @RequestParam(required = false) CommentStatus status,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.ok(commentService.listForAdmin(articleId, status, page, size));
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<Object> updateStatus(@PathVariable Long id,
                                          @Valid @RequestBody CommentStatusRequest request) {
    commentService.updateStatus(id, request.getStatus());
    auditService.log(currentUserId(), "UPDATE_COMMENT_STATUS", "Comment", String.valueOf(id), "SUCCESS", request.getStatus().name());
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Object> delete(@PathVariable Long id) {
    commentService.delete(id, null, true);
    auditService.log(currentUserId(), "DELETE_COMMENT", "Comment", String.valueOf(id), "SUCCESS", null);
    return ApiResponse.ok(null);
  }

  private Long currentUserId() {
    SecurityUser securityUser = SecurityUtils.currentUser();
    return securityUser == null ? null : securityUser.getId();
  }
}
