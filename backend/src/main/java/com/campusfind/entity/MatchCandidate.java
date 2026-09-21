package com.campusfind.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_candidates", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"lost_report_id", "found_report_id"})
})
public class MatchCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lost_report_id", nullable = false)
    private LostReport lostReport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "found_report_id", nullable = false)
    private FoundReport foundReport;

    @Column(nullable = false)
    private Integer overallScore; // 0 to 100

    private Integer categoryScore;
    private Integer colorScore;
    private Integer brandScore;
    private Integer locationScore;
    private Integer timeScore;
    private Integer semanticScore;

    @Column(columnDefinition = "TEXT")
    private String matchReasons; // Semicolon or JSON separated explainable bullet points

    @Column(length = 30, nullable = false)
    private String status = "PROPOSED"; // PROPOSED, NOTIFIED, CLAIMED, DISMISSED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public MatchCandidate() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LostReport getLostReport() { return lostReport; }
    public void setLostReport(LostReport lostReport) { this.lostReport = lostReport; }
    public FoundReport getFoundReport() { return foundReport; }
    public void setFoundReport(FoundReport foundReport) { this.foundReport = foundReport; }
    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    public Integer getCategoryScore() { return categoryScore; }
    public void setCategoryScore(Integer categoryScore) { this.categoryScore = categoryScore; }
    public Integer getColorScore() { return colorScore; }
    public void setColorScore(Integer colorScore) { this.colorScore = colorScore; }
    public Integer getBrandScore() { return brandScore; }
    public void setBrandScore(Integer brandScore) { this.brandScore = brandScore; }
    public Integer getLocationScore() { return locationScore; }
    public void setLocationScore(Integer locationScore) { this.locationScore = locationScore; }
    public Integer getTimeScore() { return timeScore; }
    public void setTimeScore(Integer timeScore) { this.timeScore = timeScore; }
    public Integer getSemanticScore() { return semanticScore; }
    public void setSemanticScore(Integer semanticScore) { this.semanticScore = semanticScore; }
    public String getMatchReasons() { return matchReasons; }
    public void setMatchReasons(String matchReasons) { this.matchReasons = matchReasons; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
