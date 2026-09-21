package com.campusfind.controller;

import com.campusfind.dto.FoundReportDtos;
import com.campusfind.entity.ReportStatus;
import com.campusfind.entity.Role;
import com.campusfind.entity.User;
import com.campusfind.repository.UserRepository;
import com.campusfind.security.UserPrincipal;
import com.campusfind.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/found")
public class FoundReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    public FoundReportController(ReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<FoundReportDtos.FoundReportResponse> createFoundReport(
            @Valid @RequestBody FoundReportDtos.CreateFoundReportRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        Long finderId = currentUser != null ? currentUser.getId() : 1L; // Fallback to demo user if anonymous
        String clientIp = httpRequest.getRemoteAddr();

        FoundReportDtos.FoundReportResponse response = reportService.createFoundReport(request, finderId, clientIp);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FoundReportDtos.FoundReportResponse>> searchFoundReports(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "locationId", required = false) Long locationId,
            @RequestParam(value = "status", required = false) ReportStatus status,
            @RequestParam(value = "query", required = false) String query,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long userId = currentUser != null ? currentUser.getId() : null;
        Role role = Role.ROLE_STUDENT;
        if (currentUser != null) {
            User user = userRepository.findById(currentUser.getId()).orElse(null);
            if (user != null) role = user.getRole();
        }

        List<FoundReportDtos.FoundReportResponse> results = reportService.searchFoundReports(category, locationId, status, query, userId, role);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoundReportDtos.FoundReportResponse> getFoundReportById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long userId = currentUser != null ? currentUser.getId() : null;
        Role role = Role.ROLE_STUDENT;
        if (currentUser != null) {
            User user = userRepository.findById(currentUser.getId()).orElse(null);
            if (user != null) role = user.getRole();
        }

        FoundReportDtos.FoundReportResponse response = reportService.getFoundReportById(id, userId, role);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<FoundReportDtos.FoundReportResponse>> getMyFoundReports(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        List<FoundReportDtos.FoundReportResponse> results = reportService.getFoundReportsByFinder(currentUser.getId());
        return ResponseEntity.ok(results);
    }
}
