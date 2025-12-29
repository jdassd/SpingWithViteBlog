package com.springwithviteblog.controller;

import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.nav.NavigationGroupDto;
import com.springwithviteblog.dto.nav.SearchEngineDto;
import com.springwithviteblog.service.NavigationService;
import com.springwithviteblog.service.SearchEngineService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/navigation")
public class PublicNavigationController {
  private final NavigationService navigationService;
  private final SearchEngineService searchEngineService;

  public PublicNavigationController(NavigationService navigationService, SearchEngineService searchEngineService) {
    this.navigationService = navigationService;
    this.searchEngineService = searchEngineService;
  }

  @GetMapping
  public ApiResponse<List<NavigationGroupDto>> listGroups() {
    return ApiResponse.ok(navigationService.listGroupsWithLinks());
  }

  @GetMapping("/search-engines")
  public ApiResponse<List<SearchEngineDto>> listSearchEngines() {
    return ApiResponse.ok(searchEngineService.listEnabled());
  }
}
