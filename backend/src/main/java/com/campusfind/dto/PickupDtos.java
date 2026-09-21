package com.campusfind.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class PickupDtos {

    public static class SchedulePickupRequest {
        @NotNull(message = "Claim ID is required")
        private Long claimId;

        @NotNull(message = "Scheduled date is required")
        private LocalDateTime scheduledDate;

        @NotBlank(message = "Time slot is required")
        private String timeSlot; // e.g. "10:00 - 10:30 AM"

        private String pickupLocation = "Campus Lost & Found Central Office (Student Center Room 102)";

        public SchedulePickupRequest() {}

        public Long getClaimId() { return claimId; }
        public void setClaimId(Long claimId) { this.claimId = claimId; }
        public LocalDateTime getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
        public String getPickupLocation() { return pickupLocation; }
        public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    }

    public static class PickupAppointmentResponse {
        private Long id;
        private Long claimId;
        private String itemTitle;
        private String itemCategory;
        private String itemReferenceId;
        private String itemImageUrl;
        private String storageLocation;
        private LocalDateTime scheduledDate;
        private String timeSlot;
        private String pickupLocation;
        private String qrToken;
        private String qrCodeBase64; // Data URL for QR display
        private String otpCode; // 6-digit collection OTP
        private String status;
        private String receiptNumber;
        private LocalDateTime collectedAt;

        public PickupAppointmentResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getClaimId() { return claimId; }
        public void setClaimId(Long claimId) { this.claimId = claimId; }
        public String getItemTitle() { return itemTitle; }
        public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }
        public String getItemCategory() { return itemCategory; }
        public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }
        public String getItemReferenceId() { return itemReferenceId; }
        public void setItemReferenceId(String itemReferenceId) { this.itemReferenceId = itemReferenceId; }
        public String getItemImageUrl() { return itemImageUrl; }
        public void setItemImageUrl(String itemImageUrl) { this.itemImageUrl = itemImageUrl; }
        public String getStorageLocation() { return storageLocation; }
        public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
        public LocalDateTime getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
        public String getPickupLocation() { return pickupLocation; }
        public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
        public String getQrToken() { return qrToken; }
        public void setQrToken(String qrToken) { this.qrToken = qrToken; }
        public String getQrCodeBase64() { return qrCodeBase64; }
        public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }
        public String getOtpCode() { return otpCode; }
        public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReceiptNumber() { return receiptNumber; }
        public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
        public LocalDateTime getCollectedAt() { return collectedAt; }
        public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }
    }

    public static class VerifyPickupRequest {
        private String qrToken;
        private String otpCode;
        private String staffNotes;

        public VerifyPickupRequest() {}

        public String getQrToken() { return qrToken; }
        public void setQrToken(String qrToken) { this.qrToken = qrToken; }
        public String getOtpCode() { return otpCode; }
        public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
        public String getStaffNotes() { return staffNotes; }
        public void setStaffNotes(String staffNotes) { this.staffNotes = staffNotes; }
    }

    public static class CollectionReceiptDto {
        private String receiptNumber;
        private String itemReferenceId;
        private String itemTitle;
        private String category;
        private String claimantName;
        private String claimantEmail;
        private String claimantStudentId;
        private String staffName;
        private String pickupLocation;
        private String storageLocation;
        private LocalDateTime collectedAt;
        private String verificationMethod; // "QR_SCAN_AND_OTP"
        private String digitalSignature;

        public CollectionReceiptDto() {}

        public String getReceiptNumber() { return receiptNumber; }
        public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
        public String getItemReferenceId() { return itemReferenceId; }
        public void setItemReferenceId(String itemReferenceId) { this.itemReferenceId = itemReferenceId; }
        public String getItemTitle() { return itemTitle; }
        public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getClaimantName() { return claimantName; }
        public void setClaimantName(String claimantName) { this.claimantName = claimantName; }
        public String getClaimantEmail() { return claimantEmail; }
        public void setClaimantEmail(String claimantEmail) { this.claimantEmail = claimantEmail; }
        public String getClaimantStudentId() { return claimantStudentId; }
        public void setClaimantStudentId(String claimantStudentId) { this.claimantStudentId = claimantStudentId; }
        public String getStaffName() { return staffName; }
        public void setStaffName(String staffName) { this.staffName = staffName; }
        public String getPickupLocation() { return pickupLocation; }
        public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
        public String getStorageLocation() { return storageLocation; }
        public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
        public LocalDateTime getCollectedAt() { return collectedAt; }
        public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }
        public String getVerificationMethod() { return verificationMethod; }
        public void setVerificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; }
        public String getDigitalSignature() { return digitalSignature; }
        public void setDigitalSignature(String digitalSignature) { this.digitalSignature = digitalSignature; }
    }
}
