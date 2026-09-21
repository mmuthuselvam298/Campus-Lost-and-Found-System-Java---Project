package com.campusfind.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_reports")
public class LostReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String referenceId; // e.g. LR-2026-001001

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(length = 50)
    private String color;

    @Column(length = 60)
    private String brand;

    @Column(length = 255)
    private String distinctiveFeatures;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private CampusLocation campusLocation;

    @Column(length = 120)
    private String specificArea;

    @Column(nullable = false)
    private LocalDateTime lostDate;

    @Column(length = 30, nullable = false)
    private String status = "ACTIVE"; // ACTIVE, MATCHED, RESOLVED, CANCELLED

    @Column(length = 255)
    private String referenceImageUrl;

    @Column(length = 100)
    private String rewardInfo;

    @Column(length = 50)
    private String contactPreference; // In-app, Email, Phone

    @Column(columnDefinition = "TEXT")
    private String embedding;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public LostReport() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
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
    public CampusLocation getCampusLocation() { return campusLocation; }
    public void setCampusLocation(CampusLocation campusLocation) { this.campusLocation = campusLocation; }
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
    public String getEmbedding() { return embedding; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
