package com.yhg.hotelbooking.domain.dashboard.controller;

import com.yhg.hotelbooking.domain.dashboard.dto.response.DashboardResponse;
import com.yhg.hotelbooking.domain.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse response = dashboardService.getDashboard();
        return ResponseEntity.ok(response);
    }
}
