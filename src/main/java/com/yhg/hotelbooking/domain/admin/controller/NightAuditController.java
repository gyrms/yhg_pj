package com.yhg.hotelbooking.domain.admin.controller;

import com.yhg.hotelbooking.domain.admin.dto.response.NightAuditResponse;
import com.yhg.hotelbooking.domain.admin.service.NightAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/night-audit")
@RequiredArgsConstructor
public class NightAuditController {
    private final NightAuditService nightAuditService;

    @GetMapping
    public ResponseEntity<NightAuditResponse> nightAudit() {
        NightAuditResponse response = nightAuditService.findCheckoutDataToday();
        return ResponseEntity.ok(response);
    }
}
