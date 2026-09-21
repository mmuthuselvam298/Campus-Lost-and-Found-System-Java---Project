package com.campusfind.dto;

import java.util.List;
import java.util.Map;

public class AdminDtos {

    public static class AdminStatsDto {
        private long activeLostReports;
        private long activeFoundReports;
        private long potentialMatches;
        private long pendingClaims;
        private long itemsReturned;
        private long itemsAwaitingPickup;
        private double recoveryRatePercentage;
        private double averageTimeToReturnDays;
        private Map<String, Long> reportsByLocation;
        private Map<String, Long> reportsByCategory;
        private List<AuditLogDto> recentActivities;

        public AdminStatsDto() {}

        public long getActiveLostReports() { return activeLostReports; }
        public void setActiveLostReports(long activeLostReports) { this.activeLostReports = activeLostReports; }
        public long getActiveFoundReports() { return activeFoundReports; }
        public void setActiveFoundReports(long activeFoundReports) { this.activeFoundReports = activeFoundReports; }
        public long getPotentialMatches() { return potentialMatches; }
        public void setPotentialMatches(long potentialMatches) { this.potentialMatches = potentialMatches; }
        public long getPendingClaims() { return pendingClaims; }
        public void setPendingClaims(long pendingClaims) { this.pendingClaims = pendingClaims; }
        public long getItemsReturned() { return itemsReturned; }
        public void setItemsReturned(long itemsReturned) { this.itemsReturned = itemsReturned; }
        public long getItemsAwaitingPickup() { return itemsAwaitingPickup; }
        public void setItemsAwaitingPickup(long itemsAwaitingPickup) { this.itemsAwaitingPickup = itemsAwaitingPickup; }
        public double getRecoveryRatePercentage() { return recoveryRatePercentage; }
        public void setRecoveryRatePercentage(double recoveryRatePercentage) { this.recoveryRatePercentage = recoveryRatePercentage; }
        public double getAverageTimeToReturnDays() { return averageTimeToReturnDays; }
        public void setAverageTimeToReturnDays(double averageTimeToReturnDays) { this.averageTimeToReturnDays = averageTimeToReturnDays; }
        public Map<String, Long> getReportsByLocation() { return reportsByLocation; }
        public void setReportsByLocation(Map<String, Long> reportsByLocation) { this.reportsByLocation = reportsByLocation; }
        public Map<String, Long> getReportsByCategory() { return reportsByCategory; }
        public void setReportsByCategory(Map<String, Long> reportsByCategory) { this.reportsByCategory = reportsByCategory; }
        public List<AuditLogDto> getRecentActivities() { return recentActivities; }
        public void setRecentActivities(List<AuditLogDto> recentActivities) { this.recentActivities = recentActivities; }
    }

    public static class AuditLogDto {
        private Long id;
        private String actorEmail;
        private String action;
        private String entityType;
        private String entityId;
        private String details;
        private String timestamp;

        public AuditLogDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getActorEmail() { return actorEmail; }
        public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
        public String getEntityId() { return entityId; }
        public void setEntityId(String entityId) { this.entityId = entityId; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    public static class ModerateReportRequest {
        private String action; // APPROVE, FLAG, ARCHIVE, UPDATE_STORAGE
        private String storageLocation;
        private String notes;

        public ModerateReportRequest() {}

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getStorageLocation() { return storageLocation; }
        public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
