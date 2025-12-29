package com.springwithviteblog.controller;

import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.nav.NavigationGroupDto;
import com.springwithviteblog.dto.nav.NavigationGroupRequest;
import com.springwithviteblog.dto.nav.NavigationLinkDto;
import com.springwithviteblog.dto.nav.NavigationLinkRequest;
import com.springwithviteblog.dto.nav.SearchEngineDto;
import com.springwithviteblog.dto.nav.SearchEngineRequest;
import com.springwithviteblog.service.NavigationService;
import com.springwithviteblog.service.SearchEngineService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/navigation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNavigationController {
  private final NavigationService navigationService;
  private final SearchEngineService searchEngineService;

  public AdminNavigationController(NavigationService navigationService, SearchEngineService searchEngineService) {
    this.navigationService = navigationService;
    this.searchEngineService = searchEngineService;
  }

  @GetMapping("/groups")
  public ApiResponse<List<NavigationGroupDto>> listGroups() {
    return ApiResponse.ok(navigationService.listGroupsWithLinks());
  }

  @PostMapping("/groups")
  public ApiResponse<NavigationGroupDto> createGroup(@Valid @RequestBody NavigationGroupRequest request) {
    return ApiResponse.ok(navigationService.createGroup(request));
  }

  @PutMapping("/groups/{id}")
  public ApiResponse<NavigationGroupDto> updateGroup(@PathVariable Long id, @Valid @RequestBody NavigationGroupRequest request) {
    return ApiResponse.ok(navigationService.updateGroup(id, request));
  }

  @DeleteMapping("/groups/{id}")
  public ApiResponse<Object> deleteGroup(@PathVariable Long id) {
    navigationService.deleteGroup(id);
    return ApiResponse.ok(null);
  }

  @PostMapping("/links")
  public ApiResponse<NavigationLinkDto> createLink(@Valid @RequestBody NavigationLinkRequest request) {
    return ApiResponse.ok(navigationService.createLink(request));
  }

  @PutMapping("/links/{id}")
  public ApiResponse<NavigationLinkDto> updateLink(@PathVariable Long id, @Valid @RequestBody NavigationLinkRequest request) {
    return ApiResponse.ok(navigationService.updateLink(id, request));
  }

  @DeleteMapping("/links/{id}")
  public ApiResponse<Object> deleteLink(@PathVariable Long id) {
    navigationService.deleteLink(id);
    return ApiResponse.ok(null);
  }

  @GetMapping("/search-engines")
  public ApiResponse<List<SearchEngineDto>> listSearchEngines() {
    return ApiResponse.ok(searchEngineService.listAll());
  }

  @PostMapping("/search-engines")
  public ApiResponse<SearchEngineDto> createSearchEngine(@Valid @RequestBody SearchEngineRequest request) {
    return ApiResponse.ok(searchEngineService.create(request));
  }

  @PutMapping("/search-engines/{id}")
  public ApiResponse<SearchEngineDto> updateSearchEngine(@PathVariable Long id, @Valid @RequestBody SearchEngineRequest request) {
    return ApiResponse.ok(searchEngineService.update(id, request));
  }

  @DeleteMapping("/search-engines/{id}")
  public ApiResponse<Object> deleteSearchEngine(@PathVariable Long id) {
    searchEngineService.delete(id);
    return ApiResponse.ok(null);
  }
}
