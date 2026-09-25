package com.campusfind.controller;

import com.campusfind.dto.PickupDtos;
import com.campusfind.security.UserPrincipal;
import com.campusfind.service.PickupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pickup")
public class PickupController {

    private final PickupService pickupService;

    public PickupController(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> schedulePickup(
            @Valid @RequestBody PickupDtos.SchedulePickupRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }

        String clientIp = getClientIp(httpRequest);
        PickupDtos.PickupAppointmentResponse response = pickupService.schedulePickup(request, currentUser.getId(), clientIp);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyPickups(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }
        List<PickupDtos.PickupAppointmentResponse> responses = pickupService.getPickupsForUser(currentUser.getId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<?> getPickupByClaimId(
            @PathVariable("claimId") Long claimId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }
        PickupDtos.PickupAppointmentResponse response = pickupService.getPickupByClaimId(claimId, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<PickupDtos.PickupAppointmentResponse>> getPendingPickups() {
        List<PickupDtos.PickupAppointmentResponse> responses = pickupService.getAllPendingPickups();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<?> verifyPickup(
            @RequestBody PickupDtos.VerifyPickupRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Staff authentication required for pickup handover verification"));
        }

        String clientIp = getClientIp(httpRequest);
        PickupDtos.CollectionReceiptDto receipt = pickupService.verifyAndCompletePickup(request, currentUser.getId(), clientIp);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/receipt/{appointmentId}")
    public ResponseEntity<?> getReceipt(
            @PathVariable("appointmentId") Long appointmentId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PickupDtos.CollectionReceiptDto receipt = pickupService.getReceipt(appointmentId);
        return ResponseEntity.ok(receipt);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
