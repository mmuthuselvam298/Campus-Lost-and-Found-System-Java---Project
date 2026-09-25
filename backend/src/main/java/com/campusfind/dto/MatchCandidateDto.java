package com.campusfind.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MatchCandidateDto {

    private Long id;
    private Long lostReportId;
    private String lostReferenceId;
    private String lostTitle;
    private String lostCategory;
    private String lostLocation;
    private LocalDateTime lostDate;
    private String lostImageUrl;

    private Long foundReportId;
    private String foundReferenceId;
    private String foundTitle;
    private String foundCategory;
    private String foundLocation;
    private LocalDateTime foundDate;
    private String foundImageUrl;

    private Integer overallScore;
    private Integer categoryScore;
    private Integer colorScore;
    private Integer brandScore;
    private Integer locationScore;
    private Integer timeScore;
    private Integer semanticScore;
    private Integer visualScore;
    private List<String> matchReasons;
    private String status;
    private LocalDateTime createdAt;

    public MatchCandidateDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLostReportId() { return lostReportId; }
    public void setLostReportId(Long lostReportId) { this.lostReportId = lostReportId; }
    public String getLostReferenceId() { return lostReferenceId; }
    public void setLostReferenceId(String lostReferenceId) { this.lostReferenceId = lostReferenceId; }
    public String getLostTitle() { return lostTitle; }
    public void setLostTitle(String lostTitle) { this.lostTitle = lostTitle; }
    public String getLostCategory() { return lostCategory; }
    public void setLostCategory(String lostCategory) { this.lostCategory = lostCategory; }
    public String getLostLocation() { return lostLocation; }
    public void setLostLocation(String lostLocation) { this.lostLocation = lostLocation; }
    public LocalDateTime getLostDate() { return lostDate; }
    public void setLostDate(LocalDateTime lostDate) { this.lostDate = lostDate; }
    public String getLostImageUrl() { return lostImageUrl; }
    public void setLostImageUrl(String lostImageUrl) { this.lostImageUrl = lostImageUrl; }
    public Long getFoundReportId() { return foundReportId; }
    public void setFoundReportId(Long foundReportId) { this.foundReportId = foundReportId; }
    public String getFoundReferenceId() { return foundReferenceId; }
    public void setFoundReferenceId(String foundReferenceId) { this.foundReferenceId = foundReferenceId; }
    public String getFoundTitle() { return foundTitle; }
    public void setFoundTitle(String foundTitle) { this.foundTitle = foundTitle; }
    public String getFoundCategory() { return foundCategory; }
    public void setFoundCategory(String foundCategory) { this.foundCategory = foundCategory; }
    public String getFoundLocation() { return foundLocation; }
    public void setFoundLocation(String foundLocation) { this.foundLocation = foundLocation; }
    public LocalDateTime getFoundDate() { return foundDate; }
    public void setFoundDate(LocalDateTime foundDate) { this.foundDate = foundDate; }
    public String getFoundImageUrl() { return foundImageUrl; }
    public void setFoundImageUrl(String foundImageUrl) { this.foundImageUrl = foundImageUrl; }
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
    public Integer getVisualScore() { return visualScore; }
    public void setVisualScore(Integer visualScore) { this.visualScore = visualScore; }
    public List<String> getMatchReasons() { return matchReasons; }
    public void setMatchReasons(List<String> matchReasons) { this.matchReasons = matchReasons; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
