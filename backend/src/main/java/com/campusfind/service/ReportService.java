package com.campusfind.service;

import com.campusfind.dto.FoundReportDtos;
import com.campusfind.dto.LostReportDtos;
import com.campusfind.entity.*;
import com.campusfind.repository.CampusLocationRepository;
import com.campusfind.repository.FoundReportRepository;
import com.campusfind.repository.LostReportRepository;
import com.campusfind.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final FoundReportRepository foundReportRepository;
    private final LostReportRepository lostReportRepository;
    private final CampusLocationRepository campusLocationRepository;
    private final UserRepository userRepository;
    private final AiMatchingService aiMatchingService;
    private final AuditService auditService;
    private final SecureRandom random = new SecureRandom();

    public ReportService(
            FoundReportRepository foundReportRepository,
            LostReportRepository lostReportRepository,
            CampusLocationRepository campusLocationRepository,
            UserRepository userRepository,
            AiMatchingService aiMatchingService,
            AuditService auditService) {
        this.foundReportRepository = foundReportRepository;
        this.lostReportRepository = lostReportRepository;
        this.campusLocationRepository = campusLocationRepository;
        this.userRepository = userRepository;
        this.aiMatchingService = aiMatchingService;
        this.auditService = auditService;
    }

    @Transactional
    public FoundReportDtos.FoundReportResponse createFoundReport(FoundReportDtos.CreateFoundReportRequest request, Long finderId, String clientIp) {
        User finder = userRepository.findById(finderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CampusLocation location = campusLocationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Campus location not found"));

        FoundReport report = new FoundReport();
        report.setReferenceId(generateReferenceId("LF"));
        report.setFinder(finder);
        report.setTitle(request.getTitle());
        report.setCategory(request.getCategory());
        report.setColor(request.getColor());
        report.setBrand(request.getBrand());
        report.setMaterial(request.getMaterial());
        report.setDistinctiveFeatures(request.getDistinctiveFeatures());
        report.setVisibleText(request.getVisibleText());
        report.setPublicDescription(request.getPublicDescription());
        report.setPrivateVerificationDetails(request.getPrivateVerificationDetails());
        report.setCampusLocation(location);
        report.setSpecificArea(request.getSpecificArea());
        report.setFoundDate(request.getFoundDate() != null ? request.getFoundDate() : LocalDateTime.now());
        report.setPossessionStatus(request.getPossessionStatus() != null ? request.getPossessionStatus() : PossessionStatus.FINDER_HOLDING);
        report.setStatus(ReportStatus.SUBMITTED);
        report.setPrimaryImageUrl(request.getPrimaryImageUrl());
        report.setThumbnailImageUrl(request.getThumbnailImageUrl());
        report.setAiConfidence(request.getAiConfidence());

        FoundReport saved = foundReportRepository.save(report);

        auditService.log(
                finder.getEmail(),
                "REPORT_FOUND_CREATED",
                "FoundReport",
                saved.getReferenceId(),
                "Created found report for " + saved.getTitle(),
                clientIp
        );

        // Run automated AI matching against open lost reports
        try {
            aiMatchingService.matchFoundReport(saved);
        } catch (Exception e) {
            // Log matching error without breaking report submission
        }

        return mapToFoundResponse(saved, finderId, finder.getRole());
    }

    @Transactional
    public LostReportDtos.LostReportResponse createLostReport(LostReportDtos.CreateLostReportRequest request, Long ownerId, String clientIp) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CampusLocation location = campusLocationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Campus location not found"));

        LostReport report = new LostReport();
        report.setReferenceId(generateReferenceId("LR"));
        report.setOwner(owner);
        report.setTitle(request.getTitle());
        report.setCategory(request.getCategory());
        report.setColor(request.getColor());
        report.setBrand(request.getBrand());
        report.setDistinctiveFeatures(request.getDistinctiveFeatures());
        report.setDescription(request.getDescription());
        report.setCampusLocation(location);
        report.setSpecificArea(request.getSpecificArea());
        report.setLostDate(request.getLostDate() != null ? request.getLostDate() : LocalDateTime.now());
        report.setStatus("ACTIVE");
        report.setReferenceImageUrl(request.getReferenceImageUrl());
        report.setRewardInfo(request.getRewardInfo());
        report.setContactPreference(request.getContactPreference());

        LostReport saved = lostReportRepository.save(report);

        auditService.log(
                owner.getEmail(),
                "REPORT_LOST_CREATED",
                "LostReport",
                saved.getReferenceId(),
                "Created lost report for " + saved.getTitle(),
                clientIp
        );

        // Run automated AI matching against open found reports
        try {
            aiMatchingService.matchLostReport(saved);
        } catch (Exception e) {
            // Log matching error without breaking report submission
        }

        return mapToLostResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FoundReportDtos.FoundReportResponse> searchFoundReports(String category, Long locationId, ReportStatus status, String query, Long currentUserId, Role currentUserRole) {
        org.springframework.data.jpa.domain.Specification<FoundReport> spec = (root, q, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase()));
            }
            if (locationId != null) {
                predicates.add(cb.equal(root.get("campusLocation").get("id"), locationId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("category")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("color"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("brand"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("distinctiveFeatures"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("publicDescription"), "")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return foundReportRepository.findAll(spec, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(r -> mapToFoundResponse(r, currentUserId, currentUserRole))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LostReportDtos.LostReportResponse> searchLostReports(String category, Long locationId, String status, String query) {
        org.springframework.data.jpa.domain.Specification<LostReport> spec = (root, q, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase()));
            }
            if (locationId != null) {
                predicates.add(cb.equal(root.get("campusLocation").get("id"), locationId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status.trim()));
            }
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("category")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("color"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("brand"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("distinctiveFeatures"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("description"), "")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return lostReportRepository.findAll(spec, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::mapToLostResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FoundReportDtos.FoundReportResponse getFoundReportById(Long id, Long currentUserId, Role currentUserRole) {
        FoundReport report = foundReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Found report not found with id: " + id));
        return mapToFoundResponse(report, currentUserId, currentUserRole);
    }

    @Transactional(readOnly = true)
    public LostReportDtos.LostReportResponse getLostReportById(Long id) {
        LostReport report = lostReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lost report not found with id: " + id));
        return mapToLostResponse(report);
    }

    @Transactional(readOnly = true)
    public List<FoundReportDtos.FoundReportResponse> getFoundReportsByFinder(Long finderId) {
        return foundReportRepository.findByFinderIdOrderByCreatedAtDesc(finderId)
                .stream()
                .map(r -> mapToFoundResponse(r, finderId, Role.ROLE_STUDENT))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LostReportDtos.LostReportResponse> getLostReportsByOwner(Long ownerId) {
        return lostReportRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(this::mapToLostResponse)
                .collect(Collectors.toList());
    }

    private String generateReferenceId(String prefix) {
        int year = Year.now().getValue();
        int randomSeq = 100000 + random.nextInt(900000);
        return String.format("%s-%d-%d", prefix, year, randomSeq);
    }

    public FoundReportDtos.FoundReportResponse mapToFoundResponse(FoundReport report, Long currentUserId, Role currentUserRole) {
        FoundReportDtos.FoundReportResponse dto = new FoundReportDtos.FoundReportResponse();
        dto.setId(report.getId());
        dto.setReferenceId(report.getReferenceId());
        dto.setFinderId(report.getFinder() != null ? report.getFinder().getId() : null);
        dto.setFinderName(report.getFinder() != null ? report.getFinder().getFullName() : "Campus Community");
        dto.setTitle(report.getTitle());
        dto.setCategory(report.getCategory());
        dto.setColor(report.getColor());
        dto.setBrand(report.getBrand());
        dto.setMaterial(report.getMaterial());
        dto.setDistinctiveFeatures(report.getDistinctiveFeatures());
        dto.setVisibleText(report.getVisibleText());
        dto.setPublicDescription(report.getPublicDescription());
        dto.setLocationId(report.getCampusLocation() != null ? report.getCampusLocation().getId() : null);
        dto.setLocationName(report.getCampusLocation() != null ? report.getCampusLocation().getName() : "Campus");
        dto.setLocationZone(report.getCampusLocation() != null ? report.getCampusLocation().getZone() : null);
        dto.setSpecificArea(report.getSpecificArea());
        dto.setFoundDate(report.getFoundDate());
        dto.setPossessionStatus(report.getPossessionStatus());
        dto.setStatus(report.getStatus());
        dto.setStorageLocation(report.getStorageLocation());
        dto.setPrimaryImageUrl(report.getPrimaryImageUrl());
        dto.setThumbnailImageUrl(report.getThumbnailImageUrl());
        dto.setAiConfidence(report.getAiConfidence());
        dto.setCreatedAt(report.getCreatedAt());

        boolean hasPrivate = report.getPrivateVerificationDetails() != null && !report.getPrivateVerificationDetails().isBlank();
        dto.setHasPrivateDetails(hasPrivate);

        // PRIVACY ENFORCEMENT: only finder or staff/admin can see the private verification details!
        boolean isAuthorized = (currentUserId != null && report.getFinder() != null && report.getFinder().getId().equals(currentUserId))
                || currentUserRole == Role.ROLE_ADMIN || currentUserRole == Role.ROLE_STAFF;

        if (isAuthorized) {
            dto.setPrivateVerificationDetails(report.getPrivateVerificationDetails());
        } else {
            dto.setPrivateVerificationDetails(null);
        }

        return dto;
    }

    private LostReportDtos.LostReportResponse mapToLostResponse(LostReport report) {
        LostReportDtos.LostReportResponse dto = new LostReportDtos.LostReportResponse();
        dto.setId(report.getId());
        dto.setReferenceId(report.getReferenceId());
        dto.setOwnerId(report.getOwner() != null ? report.getOwner().getId() : null);
        dto.setOwnerName(report.getOwner() != null ? report.getOwner().getFullName() : "Anonymous Student");
        dto.setTitle(report.getTitle());
        dto.setCategory(report.getCategory());
        dto.setColor(report.getColor());
        dto.setBrand(report.getBrand());
        dto.setDistinctiveFeatures(report.getDistinctiveFeatures());
        dto.setDescription(report.getDescription());
        dto.setLocationId(report.getCampusLocation() != null ? report.getCampusLocation().getId() : null);
        dto.setLocationName(report.getCampusLocation() != null ? report.getCampusLocation().getName() : "Campus");
        dto.setLocationZone(report.getCampusLocation() != null ? report.getCampusLocation().getZone() : null);
        dto.setSpecificArea(report.getSpecificArea());
        dto.setLostDate(report.getLostDate());
        dto.setStatus(report.getStatus());
        dto.setReferenceImageUrl(report.getReferenceImageUrl());
        dto.setRewardInfo(report.getRewardInfo());
        dto.setContactPreference(report.getContactPreference());
        dto.setCreatedAt(report.getCreatedAt());
        return dto;
    }
}
