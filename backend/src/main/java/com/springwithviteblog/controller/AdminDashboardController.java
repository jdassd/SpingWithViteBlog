package com.springwithviteblog.controller;

import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.dashboard.DashboardStatsDto;
import com.springwithviteblog.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
  private final DashboardService dashboardService;

  public AdminDashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping
  public ApiResponse<DashboardStatsDto> stats() {
    return ApiResponse.ok(dashboardService.getStats());
  }
}
