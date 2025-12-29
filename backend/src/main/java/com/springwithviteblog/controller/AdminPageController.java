package com.springwithviteblog.controller;

import com.springwithviteblog.domain.Visibility;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.page.PageDto;
import com.springwithviteblog.dto.page.PageRequest;
import com.springwithviteblog.service.PageService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/pages")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPageController {
  private final PageService pageService;

  public AdminPageController(PageService pageService) {
    this.pageService = pageService;
  }

  @GetMapping
  public ApiResponse<List<PageDto>> list(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Visibility visibility,
                                         @RequestParam(required = false) Boolean isNav) {
    return ApiResponse.ok(pageService.list(keyword, visibility, isNav));
  }

  @GetMapping("/{id}")
  public ApiResponse<PageDto> get(@PathVariable Long id) {
    return ApiResponse.ok(pageService.getById(id));
  }

  @PostMapping
  public ApiResponse<PageDto> create(@Valid @RequestBody PageRequest request) {
    return ApiResponse.ok(pageService.create(request));
  }

  @PutMapping("/{id}")
  public ApiResponse<PageDto> update(@PathVariable Long id, @Valid @RequestBody PageRequest request) {
    return ApiResponse.ok(pageService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Object> delete(@PathVariable Long id) {
    pageService.delete(id);
    return ApiResponse.ok(null);
  }
}
