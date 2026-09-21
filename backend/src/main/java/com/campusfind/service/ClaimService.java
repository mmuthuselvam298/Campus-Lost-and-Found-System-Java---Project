package com.campusfind.service;

import com.campusfind.dto.ClaimDtos;
import com.campusfind.entity.*;
import com.campusfind.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final FoundReportRepository foundReportRepository;
    private final LostReportRepository lostReportRepository;
    private final UserRepository userRepository;
    private final PickupAppointmentRepository pickupAppointmentRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public ClaimService(
            ClaimRepository claimRepository,
            FoundReportRepository foundReportRepository,
            LostReportRepository lostReportRepository,
            UserRepository userRepository,
            PickupAppointmentRepository pickupAppointmentRepository,
            NotificationService notificationService,
            AuditService auditService) {
        this.claimRepository = claimRepository;
        this.foundReportRepository = foundReportRepository;
        this.lostReportRepository = lostReportRepository;
        this.userRepository = userRepository;
        this.pickupAppointmentRepository = pickupAppointmentRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public ClaimDtos.ClaimResponse submitClaim(ClaimDtos.CreateClaimRequest request, Long claimantId, String clientIp) {
        User claimant = userRepository.findById(claimantId)
                .orElseThrow(() -> new IllegalArgumentException("Claimant not found"));

        FoundReport foundReport = foundReportRepository.findById(request.getFoundReportId())
                .orElseThrow(() -> new IllegalArgumentException("Found report not found"));

        // Prevent duplicate pending claims by the same user
        if (claimRepository.existsByFoundReportIdAndClaimantIdAndStatusNot(foundReport.getId(), claimantId, ClaimStatus.REJECTED)) {
            throw new IllegalStateException("You already have an active claim submitted for this item");
        }

        Claim claim = new Claim();
        claim.setFoundReport(foundReport);
        claim.setClaimant(claimant);
        claim.setClaimantAnswers(request.getClaimantAnswers());

        if (request.getLostReportId() != null) {
            lostReportRepository.findById(request.getLostReportId()).ifPresent(claim::setLostReport);
        }

        // AI Consistency Analysis between claimant answers and private verification details
        String privateDetails = foundReport.getPrivateVerificationDetails();
        ConsistencyResult evaluation = evaluateConsistency(request.getClaimantAnswers(), privateDetails);
        claim.setConsistencyScore(evaluation.score);
        claim.setConsistencyAnalysis(evaluation.analysis);
        claim.setStatus(ClaimStatus.SUBMITTED);

        Claim saved = claimRepository.save(claim);

        // Update found report status
        foundReport.setStatus(ReportStatus.CLAIM_PENDING);
        foundReportRepository.save(foundReport);

        // Audit log
        auditService.log(
                claimant.getEmail(),
                "CLAIM_SUBMITTED",
                "Claim",
                saved.getId().toString(),
                "Claim submitted for item " + foundReport.getReferenceId() + ". AI Consistency: " + evaluation.score,
                clientIp
        );

        // Notify finder if finder is holding the item
        if (foundReport.getFinder() != null) {
            notificationService.createNotification(
                    foundReport.getFinder(),
                    "Claim Submitted",
                    "A student submitted an ownership claim for found item " + foundReport.getReferenceId(),
                    "CLAIM_STATUS",
                    "/claims"
            );
        }

        return mapToClaimResponse(saved);
    }

    @Transactional
    public ClaimDtos.ClaimResponse reviewClaim(Long claimId, ClaimDtos.ReviewClaimRequest request, Long reviewerId, String clientIp) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));

        claim.setStatus(request.getStatus());
        claim.setAdminNotes(request.getAdminNotes());
        claim.setReviewedBy(reviewer);
        claim.setReviewedAt(LocalDateTime.now());

        FoundReport foundReport = claim.getFoundReport();
        if (request.getStorageLocation() != null && !request.getStorageLocation().isBlank()) {
            foundReport.setStorageLocation(request.getStorageLocation());
        }

        if (request.getStatus() == ClaimStatus.APPROVED) {
            foundReport.setStatus(ReportStatus.READY_FOR_PICKUP);
            notificationService.createNotification(
                    claim.getClaimant(),
                    "Claim Approved! Schedule Pickup",
                    "Your claim for " + foundReport.getTitle() + " (" + foundReport.getReferenceId() + ") was approved. Please schedule your pickup appointment.",
                    "CLAIM_STATUS",
                    "/pickup"
            );
        } else if (request.getStatus() == ClaimStatus.REJECTED) {
            foundReport.setStatus(ReportStatus.VERIFIED);
            notificationService.createNotification(
                    claim.getClaimant(),
                    "Claim Verification Update",
                    "Your claim for " + foundReport.getTitle() + " was not approved. Reason: " + (request.getAdminNotes() != null ? request.getAdminNotes() : "Verification details did not match."),
                    "CLAIM_STATUS",
                    "/claims"
            );
        }

        foundReportRepository.save(foundReport);
        Claim saved = claimRepository.save(claim);

        auditService.log(
                reviewer.getEmail(),
                "CLAIM_REVIEWED",
                "Claim",
                saved.getId().toString(),
                "Reviewed claim to status " + request.getStatus() + ". Notes: " + request.getAdminNotes(),
                clientIp
        );

        return mapToClaimResponse(saved);
    }

    public List<ClaimDtos.ClaimResponse> getClaimsForUser(Long claimantId) {
        return claimRepository.findByClaimantIdOrderByCreatedAtDesc(claimantId)
                .stream()
                .map(this::mapToClaimResponse)
                .collect(Collectors.toList());
    }

    public List<ClaimDtos.ClaimResponse> getClaimsForAdmin() {
        return claimRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::mapToClaimResponse)
                .collect(Collectors.toList());
    }

    public ClaimDtos.ClaimResponse getClaimById(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        return mapToClaimResponse(claim);
    }

    private ConsistencyResult evaluateConsistency(String answers, String privateDetails) {
        if (privateDetails == null || privateDetails.isBlank()) {
            return new ConsistencyResult("MEDIUM", "No secret verification points were specified on found report. Staff manual verification recommended.");
        }
        if (answers == null || answers.isBlank()) {
            return new ConsistencyResult("LOW", "Claimant provided no verification information.");
        }

        Set<String> privateTokens = extractTokens(privateDetails);
        Set<String> answerTokens = extractTokens(answers);

        Set<String> matches = new HashSet<>(privateTokens);
        matches.retainAll(answerTokens);

        if (matches.size() >= 2) {
            return new ConsistencyResult(
                    "HIGH",
                    "Strong verification alignment: claimant provided matching identifiers (" + String.join(", ", matches) + ") consistent with non-public item contents."
            );
        } else if (matches.size() == 1) {
            return new ConsistencyResult(
                    "MEDIUM",
                    "Partial verification alignment: matched keyword '" + matches.iterator().next() + "'. Staff should request student ID card confirmation at pickup."
            );
        } else {
            return new ConsistencyResult(
                    "LOW",
                    "Low verification alignment: claimant answers do not clearly correspond to private item metadata. Requires detailed manual inspection."
            );
        }
    }

    private Set<String> extractTokens(String text) {
        Set<String> stopWords = new HashSet<>(Arrays.asList("the", "a", "an", "and", "in", "with", "it", "has", "inside", "there", "is", "was", "are"));
        return Arrays.stream(text.toLowerCase().split("[^a-zA-Z0-9]+"))
                .filter(w -> w.length() > 2 && !stopWords.contains(w))
                .collect(Collectors.toSet());
    }

    private ClaimDtos.ClaimResponse mapToClaimResponse(Claim claim) {
        ClaimDtos.ClaimResponse dto = new ClaimDtos.ClaimResponse();
        dto.setId(claim.getId());
        dto.setFoundReportId(claim.getFoundReport().getId());
        dto.setFoundReferenceId(claim.getFoundReport().getReferenceId());
        dto.setFoundTitle(claim.getFoundReport().getTitle());
        dto.setFoundCategory(claim.getFoundReport().getCategory());
        dto.setFoundImageUrl(claim.getFoundReport().getPrimaryImageUrl());
        dto.setClaimantId(claim.getClaimant().getId());
        dto.setClaimantName(claim.getClaimant().getFullName());
        dto.setClaimantEmail(claim.getClaimant().getEmail());
        dto.setClaimantAnswers(claim.getClaimantAnswers());
        dto.setConsistencyScore(claim.getConsistencyScore());
        dto.setConsistencyAnalysis(claim.getConsistencyAnalysis());
        dto.setStatus(claim.getStatus());
        dto.setAdminNotes(claim.getAdminNotes());
        dto.setCreatedAt(claim.getCreatedAt());

        Optional<PickupAppointment> appointment = pickupAppointmentRepository.findByClaimId(claim.getId());
        dto.setPickupScheduled(appointment.isPresent());
        dto.setPickupAppointmentId(appointment.map(PickupAppointment::getId).orElse(null));

        return dto;
    }

    private static class ConsistencyResult {
        final String score;
        final String analysis;
        ConsistencyResult(String score, String analysis) {
            this.score = score;
            this.analysis = analysis;
        }
    }
}
