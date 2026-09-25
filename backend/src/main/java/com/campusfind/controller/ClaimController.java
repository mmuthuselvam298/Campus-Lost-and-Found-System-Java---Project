package com.campusfind.controller;

import com.campusfind.dto.ClaimDtos;
import com.campusfind.entity.Role;
import com.campusfind.security.UserPrincipal;
import com.campusfind.service.ClaimService;
import com.campusfind.service.RateLimitingService;
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
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;
    private final RateLimitingService rateLimitingService;

    public ClaimController(ClaimService claimService, RateLimitingService rateLimitingService) {
        this.claimService = claimService;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping
    public ResponseEntity<?> submitClaim(
            @Valid @RequestBody ClaimDtos.CreateClaimRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required to submit claims"));
        }

        String clientIp = getClientIp(httpRequest);
        if (!rateLimitingService.allowClaimRequest(currentUser.getId().toString())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Too many claim submissions. Please wait before submitting another claim."));
        }

        ClaimDtos.ClaimResponse response = claimService.submitClaim(request, currentUser.getId(), clientIp);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyClaims(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }
        List<ClaimDtos.ClaimResponse> responses = claimService.getClaimsForUser(currentUser.getId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClaimById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }

        ClaimDtos.ClaimResponse response = claimService.getClaimById(id);
        boolean isStaffOrAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"));

        boolean isClaimant = response.getClaimantId() != null && response.getClaimantId().equals(currentUser.getId());

        // PRIVACY ENFORCEMENT: Only the claimant or authorized staff/admin can view a claim record
        if (!isStaffOrAdmin && !isClaimant) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied to claim verification details"));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<ClaimDtos.ClaimResponse>> getAllClaimsForAdmin() {
        List<ClaimDtos.ClaimResponse> responses = claimService.getClaimsForAdmin();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<?> reviewClaim(
            @PathVariable("id") Long id,
            @Valid @RequestBody ClaimDtos.ReviewClaimRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest httpRequest) {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }

        String clientIp = getClientIp(httpRequest);
        ClaimDtos.ClaimResponse response = claimService.reviewClaim(id, request, currentUser.getId(), clientIp);
        return ResponseEntity.ok(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
