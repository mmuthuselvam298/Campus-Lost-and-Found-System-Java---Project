package com.campusfind.controller;

import com.campusfind.dto.ClaimDtos;
import com.campusfind.security.UserPrincipal;
import com.campusfind.service.ClaimService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<ClaimDtos.ClaimResponse> submitClaim(
            @Valid @RequestBody ClaimDtos.CreateClaimRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        Long claimantId = currentUser != null ? currentUser.getId() : 1L; // Fallback to demo student if demo testing
        String clientIp = httpRequest.getRemoteAddr();

        ClaimDtos.ClaimResponse response = claimService.submitClaim(request, claimantId, clientIp);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ClaimDtos.ClaimResponse>> getMyClaims(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long claimantId = currentUser != null ? currentUser.getId() : 1L;
        List<ClaimDtos.ClaimResponse> responses = claimService.getClaimsForUser(claimantId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimDtos.ClaimResponse> getClaimById(@PathVariable("id") Long id) {
        ClaimDtos.ClaimResponse response = claimService.getClaimById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<ClaimDtos.ClaimResponse>> getAllClaimsForAdmin() {
        List<ClaimDtos.ClaimResponse> responses = claimService.getClaimsForAdmin();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ClaimDtos.ClaimResponse> reviewClaim(
            @PathVariable("id") Long id,
            @Valid @RequestBody ClaimDtos.ReviewClaimRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        Long reviewerId = currentUser != null ? currentUser.getId() : 2L; // Fallback to staff
        String clientIp = httpRequest.getRemoteAddr();

        ClaimDtos.ClaimResponse response = claimService.reviewClaim(id, request, reviewerId, clientIp);
        return ResponseEntity.ok(response);
    }
}
