package com.campusfind.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "found_report_id", nullable = false)
    private FoundReport foundReport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lost_report_id")
    private LostReport lostReport;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String claimantAnswers; // Blind verification responses provided by claimant

    @Column(length = 20)
    private String consistencyScore; // HIGH, MEDIUM, LOW

    @Column(columnDefinition = "TEXT")
    private String consistencyAnalysis; // AI consistency reasoning

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClaimStatus status = ClaimStatus.SUBMITTED;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Claim() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FoundReport getFoundReport() { return foundReport; }
    public void setFoundReport(FoundReport foundReport) { this.foundReport = foundReport; }
    public User getClaimant() { return claimant; }
    public void setClaimant(User claimant) { this.claimant = claimant; }
    public LostReport getLostReport() { return lostReport; }
    public void setLostReport(LostReport lostReport) { this.lostReport = lostReport; }
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
    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
