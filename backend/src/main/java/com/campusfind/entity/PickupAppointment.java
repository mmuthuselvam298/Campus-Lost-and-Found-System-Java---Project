package com.campusfind.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pickup_appointments")
public class PickupAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "claim_id", nullable = false, unique = true)
    private Claim claim;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    @Column(nullable = false, length = 50)
    private String timeSlot; // e.g. "10:00 - 10:30 AM"

    @Column(nullable = false, length = 120)
    private String pickupLocation; // e.g. "Campus Lost & Found Office - Room 102"

    @Column(nullable = false, unique = true, length = 64)
    private String qrToken; // Opaque cryptographic token encoded in QR

    @Column(nullable = false, length = 10)
    private String otpCode; // 6-digit one-time code (e.g. 482731)

    @Column(length = 30, nullable = false)
    private String status = "SCHEDULED"; // SCHEDULED, COMPLETED, CANCELLED, EXPIRED

    private LocalDateTime collectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private User verifiedByStaff;

    @Column(length = 50)
    private String receiptNumber; // e.g. RCPT-2026-004821

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PickupAppointment() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getQrToken() { return qrToken; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }
    public User getVerifiedByStaff() { return verifiedByStaff; }
    public void setVerifiedByStaff(User verifiedByStaff) { this.verifiedByStaff = verifiedByStaff; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
