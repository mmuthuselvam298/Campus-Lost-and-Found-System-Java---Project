package com.campusfind.dto;

import com.campusfind.entity.ClaimStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ClaimDtos {

    public static class CreateClaimRequest {
        @NotNull(message = "Found report ID is required")
        private Long foundReportId;

        private Long lostReportId;

        @NotBlank(message = "Claim verification answers are required")
        private String claimantAnswers; // Blind verification details provided by student

        public CreateClaimRequest() {}

        public Long getFoundReportId() { return foundReportId; }
        public void setFoundReportId(Long foundReportId) { this.foundReportId = foundReportId; }
        public Long getLostReportId() { return lostReportId; }
        public void setLostReportId(Long lostReportId) { this.lostReportId = lostReportId; }
        public String getClaimantAnswers() { return claimantAnswers; }
        public void setClaimantAnswers(String claimantAnswers) { this.claimantAnswers = claimantAnswers; }
    }

    public static class ClaimResponse {
        private Long id;
        private Long foundReportId;
        private String foundReferenceId;
        private String foundTitle;
        private String foundCategory;
        private String foundImageUrl;
        private Long claimantId;
        private String claimantName;
        private String claimantEmail;
        private String claimantAnswers;
        private String consistencyScore; // HIGH, MEDIUM, LOW
        private String consistencyAnalysis;
        private ClaimStatus status;
        private String adminNotes;
        private LocalDateTime createdAt;
        private boolean pickupScheduled;
        private Long pickupAppointmentId;

        public ClaimResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getFoundReportId() { return foundReportId; }
        public void setFoundReportId(Long foundReportId) { this.foundReportId = foundReportId; }
        public String getFoundReferenceId() { return foundReferenceId; }
        public void setFoundReferenceId(String foundReferenceId) { this.foundReferenceId = foundReferenceId; }
        public String getFoundTitle() { return foundTitle; }
        public void setFoundTitle(String foundTitle) { this.foundTitle = foundTitle; }
        public String getFoundCategory() { return foundCategory; }
        public void setFoundCategory(String foundCategory) { this.foundCategory = foundCategory; }
        public String getFoundImageUrl() { return foundImageUrl; }
        public void setFoundImageUrl(String foundImageUrl) { this.foundImageUrl = foundImageUrl; }
        public Long getClaimantId() { return claimantId; }
        public void setClaimantId(Long claimantId) { this.claimantId = claimantId; }
        public String getClaimantName() { return claimantName; }
        public void setClaimantName(String claimantName) { this.claimantName = claimantName; }
        public String getClaimantEmail() { return claimantEmail; }
        public void setClaimantEmail(String claimantEmail) { this.claimantEmail = claimantEmail; }
        public String getClaimantAnswers() { return claimantAnswers; }
        public void setClaimantAnswers(String claimantAnswers) { this.claimantAnswers = claimantAnswers; }
        public String getConsistencyScore() { return consistencyScore; }
        public void setConsistencyScore(String consistencyScore) { this.consistencyScore = consistencyScore; }
        public String getConsistencyAnalysis() { return consistencyAnalysis; }
        public void setConsistencyAnalysis(String consistencyAnalysis) { this.consistencyAnalysis = consistencyAnalysis; }
        public ClaimStatus getStatus() { return status; }
        public void setStatus(ClaimStatus status) { this.status = status; }
        public String getAdminNotes() { return adminNotes; }
        public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public boolean isPickupScheduled() { return pickupScheduled; }
        public void setPickupScheduled(boolean pickupScheduled) { this.pickupScheduled = pickupScheduled; }
        public Long getPickupAppointmentId() { return pickupAppointmentId; }
        public void setPickupAppointmentId(Long pickupAppointmentId) { this.pickupAppointmentId = pickupAppointmentId; }
    }

    public static class ReviewClaimRequest {
        @NotNull(message = "Claim status is required")
        private ClaimStatus status;

        private String adminNotes;
        private String storageLocation; // Optional update to storage shelf/room

        public ReviewClaimRequest() {}

        public ClaimStatus getStatus() { return status; }
        public void setStatus(ClaimStatus status) { this.status = status; }
        public String getAdminNotes() { return adminNotes; }
        public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
        public String getStorageLocation() { return storageLocation; }
        public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    }
}
