package com.campusfind.service;

import com.campusfind.dto.AdminDtos;
import com.campusfind.entity.*;
import com.campusfind.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final LostReportRepository lostReportRepository;
    private final FoundReportRepository foundReportRepository;
    private final MatchCandidateRepository matchCandidateRepository;
    private final ClaimRepository claimRepository;
    private final PickupAppointmentRepository pickupAppointmentRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public AdminService(
            LostReportRepository lostReportRepository,
            FoundReportRepository foundReportRepository,
            MatchCandidateRepository matchCandidateRepository,
            ClaimRepository claimRepository,
            PickupAppointmentRepository pickupAppointmentRepository,
            AuditLogRepository auditLogRepository,
            UserRepository userRepository,
            AuditService auditService) {
        this.lostReportRepository = lostReportRepository;
        this.foundReportRepository = foundReportRepository;
        this.matchCandidateRepository = matchCandidateRepository;
        this.claimRepository = claimRepository;
        this.pickupAppointmentRepository = pickupAppointmentRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    public AdminDtos.AdminStatsDto getAdminStats() {
        AdminDtos.AdminStatsDto stats = new AdminDtos.AdminStatsDto();

        long activeLost = lostReportRepository.countByStatus("ACTIVE");
        long activeFound = foundReportRepository.countByStatus(ReportStatus.SUBMITTED)
                + foundReportRepository.countByStatus(ReportStatus.VERIFIED);
        long matches = matchCandidateRepository.count();
        long pendingClaims = claimRepository.countByStatus(ClaimStatus.SUBMITTED)
                + claimRepository.countByStatus(ClaimStatus.UNDER_REVIEW);
        long returned = foundReportRepository.countByStatus(ReportStatus.RETURNED);
        long awaitingPickup = foundReportRepository.countByStatus(ReportStatus.READY_FOR_PICKUP);

        long totalFound = foundReportRepository.count();
        double recoveryRate = totalFound > 0 ? ((double) returned / totalFound) * 100.0 : 0.0;

        // Calculate average turnaround time from completed pickups
        List<PickupAppointment> completed = pickupAppointmentRepository.findByStatusOrderByScheduledDateAsc("COMPLETED");
        double avgDays = 0.0;
        if (!completed.isEmpty()) {
            long totalHours = 0;
            int validCount = 0;
            for (PickupAppointment p : completed) {
                if (p.getCollectedAt() != null && p.getClaim() != null && p.getClaim().getFoundReport() != null) {
                    long hours = Duration.between(p.getClaim().getFoundReport().getCreatedAt(), p.getCollectedAt()).toHours();
                    totalHours += Math.max(0, hours);
                    validCount++;
                }
            }
            if (validCount > 0) {
                avgDays = (double) totalHours / (validCount * 24.0);
            }
        }

        // Location Distribution
        Map<String, Long> byLoc = new LinkedHashMap<>();
        for (Object[] row : foundReportRepository.countReportsByLocation()) {
            if (row[0] != null) byLoc.put(row[0].toString(), (Long) row[1]);
        }

        // Category Distribution
        Map<String, Long> byCat = new LinkedHashMap<>();
        for (Object[] row : foundReportRepository.countReportsByCategory()) {
            if (row[0] != null) byCat.put(row[0].toString(), (Long) row[1]);
        }

        // Recent Audit Activities
        List<AdminDtos.AuditLogDto> auditDtos = auditLogRepository.findTop50ByOrderByTimestampDesc()
                .stream()
                .map(this::mapAuditLog)
                .collect(Collectors.toList());

        stats.setActiveLostReports(activeLost);
        stats.setActiveFoundReports(activeFound);
        stats.setPotentialMatches(matches);
        stats.setPendingClaims(pendingClaims);
        stats.setItemsReturned(returned);
        stats.setItemsAwaitingPickup(awaitingPickup);
        stats.setRecoveryRatePercentage(Math.round(recoveryRate * 10.0) / 10.0);
        stats.setAverageTimeToReturnDays(Math.round(avgDays * 10.0) / 10.0);
        stats.setReportsByLocation(byLoc);
        stats.setReportsByCategory(byCat);
        stats.setRecentActivities(auditDtos);

        return stats;
    }

    @Transactional
    public void moderateReport(Long reportId, AdminDtos.ModerateReportRequest request, Long adminId, String clientIp) {
        FoundReport report = foundReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Found report not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));

        if ("APPROVE".equalsIgnoreCase(request.getAction())) {
            report.setStatus(ReportStatus.VERIFIED);
        } else if ("FLAG".equalsIgnoreCase(request.getAction())) {
            report.setStatus(ReportStatus.CLAIM_PENDING);
        } else if ("ARCHIVE".equalsIgnoreCase(request.getAction())) {
            report.setStatus(ReportStatus.EXPIRED);
        }

        if (request.getStorageLocation() != null && !request.getStorageLocation().isBlank()) {
            report.setStorageLocation(request.getStorageLocation());
        }

        foundReportRepository.save(report);

        auditService.log(
                admin.getEmail(),
                "MODERATE_REPORT",
                "FoundReport",
                report.getReferenceId(),
                "Action: " + request.getAction() + ", Notes: " + request.getNotes() + ", Storage: " + report.getStorageLocation(),
                clientIp
        );
    }

    public List<AdminDtos.AuditLogDto> getAuditLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc()
                .stream()
                .map(this::mapAuditLog)
                .collect(Collectors.toList());
    }

    private AdminDtos.AuditLogDto mapAuditLog(AuditLog log) {
        AdminDtos.AuditLogDto dto = new AdminDtos.AuditLogDto();
        dto.setId(log.getId());
        dto.setActorEmail(log.getActorEmail());
        dto.setAction(log.getAction());
        dto.setEntityType(log.getEntityType());
        dto.setEntityId(log.getEntityId());
        dto.setDetails(log.getDetails());
        dto.setTimestamp(log.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return dto;
    }
}
