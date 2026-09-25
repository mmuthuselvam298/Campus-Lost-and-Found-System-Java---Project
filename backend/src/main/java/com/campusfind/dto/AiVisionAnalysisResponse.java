package com.campusfind.dto;

import java.util.List;

public class AiVisionAnalysisResponse {

    private String category;
    private String subcategory;
    private String color;
    private String material;
    private String brand;
    private String visibleText;
    private String distinctiveFeatures;
    private List<String> observedFeatures;
    private List<String> inferredFeatures;
    private Double confidenceScore;
    private String confidenceLevel; // High confidence, Medium confidence, Low confidence
    private List<String> verificationQuestions;
    private String locationSuggestion;
    private String locationReason;
    private ImageQualityAssessment imageQuality;
    private boolean aiAvailable = true;
    private String aiMessage;

    public static class ImageQualityAssessment {
        private boolean adequate = true;
        private String lightingCondition = "Good";
        private String clarity = "Sharp";
        private String suggestion = "Image clarity is sufficient for item identification.";

        public ImageQualityAssessment() {}

        public ImageQualityAssessment(boolean adequate, String lightingCondition, String clarity, String suggestion) {
            this.adequate = adequate;
            this.lightingCondition = lightingCondition;
            this.clarity = clarity;
            this.suggestion = suggestion;
        }

        public boolean isAdequate() { return adequate; }
        public void setAdequate(boolean adequate) { this.adequate = adequate; }
        public String getLightingCondition() { return lightingCondition; }
        public void setLightingCondition(String lightingCondition) { this.lightingCondition = lightingCondition; }
        public String getClarity() { return clarity; }
        public void setClarity(String clarity) { this.clarity = clarity; }
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    }

    public AiVisionAnalysisResponse() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getVisibleText() { return visibleText; }
    public void setVisibleText(String visibleText) { this.visibleText = visibleText; }
    public String getDistinctiveFeatures() { return distinctiveFeatures; }
    public void setDistinctiveFeatures(String distinctiveFeatures) { this.distinctiveFeatures = distinctiveFeatures; }
    public List<String> getObservedFeatures() { return observedFeatures; }
    public void setObservedFeatures(List<String> observedFeatures) { this.observedFeatures = observedFeatures; }
    public List<String> getInferredFeatures() { return inferredFeatures; }
    public void setInferredFeatures(List<String> inferredFeatures) { this.inferredFeatures = inferredFeatures; }
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    public String getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public List<String> getVerificationQuestions() { return verificationQuestions; }
    public void setVerificationQuestions(List<String> verificationQuestions) { this.verificationQuestions = verificationQuestions; }
    public String getLocationSuggestion() { return locationSuggestion; }
    public void setLocationSuggestion(String locationSuggestion) { this.locationSuggestion = locationSuggestion; }
    public String getLocationReason() { return locationReason; }
    public void setLocationReason(String locationReason) { this.locationReason = locationReason; }
    public ImageQualityAssessment getImageQuality() { return imageQuality; }
    public void setImageQuality(ImageQualityAssessment imageQuality) { this.imageQuality = imageQuality; }
    public boolean isAiAvailable() { return aiAvailable; }
    public void setAiAvailable(boolean aiAvailable) { this.aiAvailable = aiAvailable; }
    public String getAiMessage() { return aiMessage; }
    public void setAiMessage(String aiMessage) { this.aiMessage = aiMessage; }
}
