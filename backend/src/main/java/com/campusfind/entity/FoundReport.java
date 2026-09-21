package com.campusfind.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "found_reports")
public class FoundReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String referenceId; // e.g. LF-2026-001001

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "finder_id", nullable = false)
    private User finder;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(length = 50)
    private String color;

    @Column(length = 60)
    private String brand;

    @Column(length = 60)
    private String material;

    @Column(length = 255)
    private String distinctiveFeatures;

    @Column(length = 255)
    private String visibleText;

    @Column(columnDefinition = "TEXT")
    private String publicDescription;

    // PRIVACY AWARE: secret identifying details stored for anti-fraud claim verification
    @Column(columnDefinition = "TEXT")
    private String privateVerificationDetails;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private CampusLocation campusLocation;

    @Column(length = 120)
    private String specificArea; // e.g. 2nd Floor study room

    @Column(nullable = false)
    private LocalDateTime foundDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PossessionStatus possessionStatus = PossessionStatus.FINDER_HOLDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportStatus status = ReportStatus.SUBMITTED;

    @Column(length = 100)
    private String storageLocation; // Physical bin/shelf: e.g. "Room A, Shelf S-12"

    @Column(length = 255)
    private String primaryImageUrl;

    @Column(length = 255)
    private String thumbnailImageUrl;

    private Double aiConfidence;

    @Column(columnDefinition = "TEXT")
    private String embedding; // Stored vector representation as comma-separated floats

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public FoundReport() {}

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
    public User getFinder() { return finder; }
    public void setFinder(User finder) { this.finder = finder; }
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
    public CampusLocation getCampusLocation() { return campusLocation; }
    public void setCampusLocation(CampusLocation campusLocation) { this.campusLocation = campusLocation; }
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
    public String getEmbedding() { return embedding; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
