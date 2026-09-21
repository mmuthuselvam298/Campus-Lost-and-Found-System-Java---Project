package com.campusfind.dto;

import com.campusfind.entity.PossessionStatus;
import com.campusfind.entity.ReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class FoundReportDtos {

    public static class CreateFoundReportRequest {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Category is required")
        private String category;

        private String color;
        private String brand;
        private String material;
        private String distinctiveFeatures;
        private String visibleText;
        private String publicDescription;

        // Private verification details (secret item info for anti-fraud check)
        private String privateVerificationDetails;

        @NotNull(message = "Location ID is required")
        private Long locationId;

        private String specificArea;

        @NotNull(message = "Found date is required")
        private LocalDateTime foundDate;

        private PossessionStatus possessionStatus = PossessionStatus.FINDER_HOLDING;
        private String primaryImageUrl;
        private String thumbnailImageUrl;
        private Double aiConfidence;

        public CreateFoundReportRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }
        public String getDistinctiveFeatures() { return distinctiveFeatures; }
        public void setDistinctiveFeatures(String distinctiveFeatures) { this.distinctiveFeatures = distinctiveFeatures; }
        public String getVisibleText() { return visibleText; }
        public void setVisibleText(String visibleText) { this.visibleText = visibleText; }
        public String getPublicDescription() { return publicDescription; }
        public void setPublicDescription(String publicDescription) { this.publicDescription = publicDescription; }
        public String getPrivateVerificationDetails() { return privateVerificationDetails; }
        public void setPrivateVerificationDetails(String privateVerificationDetails) { this.privateVerificationDetails = privateVerificationDetails; }
        public Long getLocationId() { return locationId; }
        public void setLocationId(Long locationId) { this.locationId = locationId; }
        public String getSpecificArea() { return specificArea; }
        public void setSpecificArea(String specificArea) { this.specificArea = specificArea; }
        public LocalDateTime getFoundDate() { return foundDate; }
        public void setFoundDate(LocalDateTime foundDate) { this.foundDate = foundDate; }
        public PossessionStatus getPossessionStatus() { return possessionStatus; }
        public void setPossessionStatus(PossessionStatus possessionStatus) { this.possessionStatus = possessionStatus; }
        public String getPrimaryImageUrl() { return primaryImageUrl; }
        public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
        public String getThumbnailImageUrl() { return thumbnailImageUrl; }
        public void setThumbnailImageUrl(String thumbnailImageUrl) { this.thumbnailImageUrl = thumbnailImageUrl; }
        public Double getAiConfidence() { return aiConfidence; }
        public void setAiConfidence(Double aiConfidence) { this.aiConfidence = aiConfidence; }
    }

    public static class FoundReportResponse {
        private Long id;
        private String referenceId;
        private Long finderId;
        private String finderName;
        private String title;
        private String category;
        private String color;
        private String brand;
        private String material;
        private String distinctiveFeatures;
        private String visibleText;
        private String publicDescription;
        private boolean hasPrivateDetails;
        private String privateVerificationDetails; // Masked for public
        private Long locationId;
        private String locationName;
        private String locationZone;
        private String specificArea;
        private LocalDateTime foundDate;
        private PossessionStatus possessionStatus;
        private ReportStatus status;
        private String storageLocation;
        private String primaryImageUrl;
        private String thumbnailImageUrl;
        private Double aiConfidence;
        private LocalDateTime createdAt;

        public FoundReportResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getReferenceId() { return referenceId; }
        public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
        public Long getFinderId() { return finderId; }
        public void setFinderId(Long finderId) { this.finderId = finderId; }
        public String getFinderName() { return finderName; }
        public void setFinderName(String finderName) { this.finderName = finderName; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }
        public String getDistinctiveFeatures() { return distinctiveFeatures; }
        public void setDistinctiveFeatures(String distinctiveFeatures) { this.distinctiveFeatures = distinctiveFeatures; }
        public String getVisibleText() { return visibleText; }
        public void setVisibleText(String visibleText) { this.visibleText = visibleText; }
        public String getPublicDescription() { return publicDescription; }
        public void setPublicDescription(String publicDescription) { this.publicDescription = publicDescription; }
        public boolean isHasPrivateDetails() { return hasPrivateDetails; }
        public void setHasPrivateDetails(boolean hasPrivateDetails) { this.hasPrivateDetails = hasPrivateDetails; }
        public String getPrivateVerificationDetails() { return privateVerificationDetails; }
        public void setPrivateVerificationDetails(String privateVerificationDetails) { this.privateVerificationDetails = privateVerificationDetails; }
        public Long getLocationId() { return locationId; }
        public void setLocationId(Long locationId) { this.locationId = locationId; }
        public String getLocationName() { return locationName; }
        public void setLocationName(String locationName) { this.locationName = locationName; }
        public String getLocationZone() { return locationZone; }
        public void setLocationZone(String locationZone) { this.locationZone = locationZone; }
        public String getSpecificArea() { return specificArea; }
        public void setSpecificArea(String specificArea) { this.specificArea = specificArea; }
        public LocalDateTime getFoundDate() { return foundDate; }
        public void setFoundDate(LocalDateTime foundDate) { this.foundDate = foundDate; }
        public PossessionStatus getPossessionStatus() { return possessionStatus; }
        public void setPossessionStatus(PossessionStatus possessionStatus) { this.possessionStatus = possessionStatus; }
        public ReportStatus getStatus() { return status; }
        public void setStatus(ReportStatus status) { this.status = status; }
        public String getStorageLocation() { return storageLocation; }
        public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
        public String getPrimaryImageUrl() { return primaryImageUrl; }
        public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
        public String getThumbnailImageUrl() { return thumbnailImageUrl; }
        public void setThumbnailImageUrl(String thumbnailImageUrl) { this.thumbnailImageUrl = thumbnailImageUrl; }
        public Double getAiConfidence() { return aiConfidence; }
        public void setAiConfidence(Double aiConfidence) { this.aiConfidence = aiConfidence; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
