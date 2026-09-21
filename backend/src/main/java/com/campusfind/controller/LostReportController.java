package com.campusfind.controller;

import com.campusfind.dto.LostReportDtos;
import com.campusfind.security.UserPrincipal;
import com.campusfind.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lost")
public class LostReportController {

    private final ReportService reportService;

    public LostReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<LostReportDtos.LostReportResponse> createLostReport(
            @Valid @RequestBody LostReportDtos.CreateLostReportRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        Long ownerId = currentUser != null ? currentUser.getId() : 1L; // Fallback to demo user if anonymous
        String clientIp = httpRequest.getRemoteAddr();

        LostReportDtos.LostReportResponse response = reportService.createLostReport(request, ownerId, clientIp);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LostReportDtos.LostReportResponse>> searchLostReports(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "locationId", required = false) Long locationId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "query", required = false) String query) {

        List<LostReportDtos.LostReportResponse> results = reportService.searchLostReports(category, locationId, status, query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LostReportDtos.LostReportResponse> getLostReportById(@PathVariable("id") Long id) {
        LostReportDtos.LostReportResponse response = reportService.getLostReportById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<LostReportDtos.LostReportResponse>> getMyLostReports(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        List<LostReportDtos.LostReportResponse> results = reportService.getLostReportsByOwner(currentUser.getId());
        return ResponseEntity.ok(results);
    }
}
