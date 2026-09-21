package com.campusfind.controller;

import com.campusfind.dto.PickupDtos;
import com.campusfind.security.UserPrincipal;
import com.campusfind.service.PickupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pickup")
public class PickupController {

    private final PickupService pickupService;

    public PickupController(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<PickupDtos.PickupAppointmentResponse> schedulePickup(
            @Valid @RequestBody PickupDtos.SchedulePickupRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        Long userId = currentUser != null ? currentUser.getId() : 1L; // Fallback to demo student
        String clientIp = httpRequest.getRemoteAddr();

        PickupDtos.PickupAppointmentResponse response = pickupService.schedulePickup(request, userId, clientIp);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PickupDtos.PickupAppointmentResponse>> getMyPickups(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long userId = currentUser != null ? currentUser.getId() : 1L;
        List<PickupDtos.PickupAppointmentResponse> responses = pickupService.getPickupsForUser(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<PickupDtos.PickupAppointmentResponse> getPickupByClaimId(
            @PathVariable("claimId") Long claimId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long userId = currentUser != null ? currentUser.getId() : 1L;
        PickupDtos.PickupAppointmentResponse response = pickupService.getPickupByClaimId(claimId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PickupDtos.PickupAppointmentResponse>> getPendingPickups() {
        List<PickupDtos.PickupAppointmentResponse> responses = pickupService.getAllPendingPickups();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/verify")
    public ResponseEntity<PickupDtos.CollectionReceiptDto> verifyPickup(
            @RequestBody PickupDtos.VerifyPickupRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        Long staffId = currentUser != null ? currentUser.getId() : 2L; // Fallback to staff
        String clientIp = httpRequest.getRemoteAddr();

        PickupDtos.CollectionReceiptDto receipt = pickupService.verifyAndCompletePickup(request, staffId, clientIp);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/receipt/{appointmentId}")
    public ResponseEntity<PickupDtos.CollectionReceiptDto> getReceipt(@PathVariable("appointmentId") Long appointmentId) {
        PickupDtos.CollectionReceiptDto receipt = pickupService.getReceipt(appointmentId);
        return ResponseEntity.ok(receipt);
    }
}
