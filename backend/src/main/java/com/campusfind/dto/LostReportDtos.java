package com.campusfind.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class LostReportDtos {

    public static class CreateLostReportRequest {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Category is required")
        private String category;

        private String color;
        private String brand;
        private String distinctiveFeatures;

        @NotBlank(message = "Description is required")
        private String description;

        @NotNull(message = "Location ID is required")
        private Long locationId;

        private String specificArea;

        @NotNull(message = "Lost date is required")
        private LocalDateTime lostDate;

        private String referenceImageUrl;
        private String rewardInfo;
        private String contactPreference = "In-App";

        public CreateLostReportRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getDistinctiveFeatures() { return distinctiveFeatures; }
        public void setDistinctiveFeatures(String distinctiveFeatures) { this.distinctiveFeatures = distinctiveFeatures; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getLocationId() { return locationId; }
        public void setLocationId(Long locationId) { this.locationId = locationId; }
        public String getSpecificArea() { return specificArea; }
        public void setSpecificArea(String specificArea) { this.specificArea = specificArea; }
        public LocalDateTime getLostDate() { return lostDate; }
        public void setLostDate(LocalDateTime lostDate) { this.lostDate = lostDate; }
        public String getReferenceImageUrl() { return referenceImageUrl; }
        public void setReferenceImageUrl(String referenceImageUrl) { this.referenceImageUrl = referenceImageUrl; }
        public String getRewardInfo() { return rewardInfo; }
        public void setRewardInfo(String rewardInfo) { this.rewardInfo = rewardInfo; }
        public String getContactPreference() { return contactPreference; }
        public void setContactPreference(String contactPreference) { this.contactPreference = contactPreference; }
    }

    public static class LostReportResponse {
        private Long id;
        private String referenceId;
        private Long ownerId;
        private String ownerName;
        private String title;
        private String category;
        private String color;
        private String brand;
        private String distinctiveFeatures;
        private String description;
        private Long locationId;
        private String locationName;
        private String locationZone;
        private String specificArea;
        private LocalDateTime lostDate;
        private String status;
        private String referenceImageUrl;
        private String rewardInfo;
        private String contactPreference;
        private LocalDateTime createdAt;

        public LostReportResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getReferenceId() { return referenceId; }
        public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
        public Long getOwnerId() { return ownerId; }
        public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
        public String getOwnerName() { return ownerName; }
        public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getDistinctiveFeatures() { return distinctiveFeatures; }
        public void setDistinctiveFeatures(String distinctiveFeatures) { this.distinctiveFeatures = distinctiveFeatures; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getLocationId() { return locationId; }
        public void setLocationId(Long locationId) { this.locationId = locationId; }
        public String getLocationName() { return locationName; }
        public void setLocationName(String locationName) { this.locationName = locationName; }
        public String getLocationZone() { return locationZone; }
        public void setLocationZone(String locationZone) { this.locationZone = locationZone; }
        public String getSpecificArea() { return specificArea; }
        public void setSpecificArea(String specificArea) { this.specificArea = specificArea; }
        public LocalDateTime getLostDate() { return lostDate; }
        public void setLostDate(LocalDateTime lostDate) { this.lostDate = lostDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReferenceImageUrl() { return referenceImageUrl; }
        public void setReferenceImageUrl(String referenceImageUrl) { this.referenceImageUrl = referenceImageUrl; }
        public String getRewardInfo() { return rewardInfo; }
        public void setRewardInfo(String rewardInfo) { this.rewardInfo = rewardInfo; }
        public String getContactPreference() { return contactPreference; }
        public void setContactPreference(String contactPreference) { this.contactPreference = contactPreference; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
