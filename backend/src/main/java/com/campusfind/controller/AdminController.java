package com.campusfind.controller;

import com.campusfind.dto.AdminDtos;
import com.campusfind.security.UserPrincipal;
import com.campusfind.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminDtos.AdminStatsDto> getStats() {
        AdminDtos.AdminStatsDto stats = adminService.getAdminStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/moderate/{reportId}")
    public ResponseEntity<Void> moderateReport(
            @PathVariable("reportId") Long reportId,
            @RequestBody AdminDtos.ModerateReportRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        Long adminId = currentUser != null ? currentUser.getId() : 3L; // Fallback to admin user
        String clientIp = httpRequest.getRemoteAddr();

        adminService.moderateReport(reportId, request, adminId, clientIp);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AdminDtos.AuditLogDto>> getAuditLogs() {
        return ResponseEntity.ok(adminService.getAuditLogs());
    }
}
