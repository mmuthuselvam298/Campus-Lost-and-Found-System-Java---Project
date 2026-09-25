package com.campusfind.dto;

import java.util.List;

public class AiDescriptionDtos {

    public static class DescriptionAssistRequest {
        private String text;

        public DescriptionAssistRequest() {}
        public DescriptionAssistRequest(String text) { this.text = text; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class DescriptionAssistResponse {
        private String category;
        private String color;
        private String brand;
        private String suggestedLocation;
        private String structuredDescription;
        private List<String> extractedKeywords;
        private List<String> clarifyingQuestions;
        private boolean aiAvailable = true;
        private String aiMessage;

        public DescriptionAssistResponse() {}

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getSuggestedLocation() { return suggestedLocation; }
        public void setSuggestedLocation(String suggestedLocation) { this.suggestedLocation = suggestedLocation; }
        public String getStructuredDescription() { return structuredDescription; }
        public void setStructuredDescription(String structuredDescription) { this.structuredDescription = structuredDescription; }
        public List<String> getExtractedKeywords() { return extractedKeywords; }
        public void setExtractedKeywords(List<String> extractedKeywords) { this.extractedKeywords = extractedKeywords; }
        public List<String> getClarifyingQuestions() { return clarifyingQuestions; }
        public void setClarifyingQuestions(List<String> clarifyingQuestions) { this.clarifyingQuestions = clarifyingQuestions; }
        public boolean isAiAvailable() { return aiAvailable; }
        public void setAiAvailable(boolean aiAvailable) { this.aiAvailable = aiAvailable; }
        public String getAiMessage() { return aiMessage; }
        public void setAiMessage(String aiMessage) { this.aiMessage = aiMessage; }
    }
}
