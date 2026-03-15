package com.library.controller;

import com.library.dto.response.DashboardResponse;
import com.library.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DashboardController - provides dashboard statistics.
 *
 * Admin gets system-wide stats.
 * Users get their personal borrow/fine stats.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard/admin
     * Admin-only: system-wide statistics.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin dashboard stats")
    public ResponseEntity<DashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    /**
     * GET /api/dashboard/user
     * Any authenticated user: personal statistics.
     */
    @GetMapping("/user")
    @Operation(summary = "User personal dashboard")
    public ResponseEntity<DashboardResponse> getUserDashboard(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getUserDashboard(authentication.getName()));
    }
}
