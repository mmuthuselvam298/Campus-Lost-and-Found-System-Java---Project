package com.campusfind.service;

import com.campusfind.dto.AiDescriptionDtos;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiDescriptionServiceImpl implements AiDescriptionService {

    private static final Map<String, String> CATEGORY_PATTERNS = new LinkedHashMap<>();
    private static final List<String> COLORS = Arrays.asList(
            "black", "blue", "red", "white", "silver", "grey", "gray", "green", "yellow", "brown", "navy", "pink", "purple"
    );
    private static final Map<String, String> LOCATION_PATTERNS = new LinkedHashMap<>();

    static {
        CATEGORY_PATTERNS.put("backpack", "Bags");
        CATEGORY_PATTERNS.put("bag", "Bags");
        CATEGORY_PATTERNS.put("wallet", "Wallets");
        CATEGORY_PATTERNS.put("purse", "Wallets");
        CATEGORY_PATTERNS.put("bottle", "Bottles");
        CATEGORY_PATTERNS.put("flask", "Bottles");
        CATEGORY_PATTERNS.put("phone", "Phones");
        CATEGORY_PATTERNS.put("mobile", "Phones");
        CATEGORY_PATTERNS.put("iphone", "Phones");
        CATEGORY_PATTERNS.put("card", "ID Cards");
        CATEGORY_PATTERNS.put("badge", "ID Cards");
        CATEGORY_PATTERNS.put("id", "ID Cards");
        CATEGORY_PATTERNS.put("laptop", "Electronics");
        CATEGORY_PATTERNS.put("macbook", "Electronics");
        CATEGORY_PATTERNS.put("charger", "Electronics");
        CATEGORY_PATTERNS.put("airpod", "Electronics");
        CATEGORY_PATTERNS.put("headphone", "Electronics");
        CATEGORY_PATTERNS.put("earphone", "Electronics");
        CATEGORY_PATTERNS.put("keys", "Keys");
        CATEGORY_PATTERNS.put("key", "Keys");
        CATEGORY_PATTERNS.put("umbrella", "Umbrellas");
        CATEGORY_PATTERNS.put("book", "Books");
        CATEGORY_PATTERNS.put("notebook", "Books");
        CATEGORY_PATTERNS.put("jacket", "Clothing");
        CATEGORY_PATTERNS.put("hoodie", "Clothing");
        CATEGORY_PATTERNS.put("calculator", "Electronics");

        LOCATION_PATTERNS.put("library", "Central Library");
        LOCATION_PATTERNS.put("cafeteria", "Student Cafeteria");
        LOCATION_PATTERNS.put("canteen", "Student Cafeteria");
        LOCATION_PATTERNS.put("mess", "Student Cafeteria");
        LOCATION_PATTERNS.put("lab", "Computer Science Lab Block");
        LOCATION_PATTERNS.put("hostel", "Hostel Block A");
        LOCATION_PATTERNS.put("dorm", "Hostel Block A");
        LOCATION_PATTERNS.put("gym", "Sports Complex");
        LOCATION_PATTERNS.put("ground", "Sports Complex");
        LOCATION_PATTERNS.put("gate", "Campus Main Gate");
        LOCATION_PATTERNS.put("bus", "Campus Main Gate");
        LOCATION_PATTERNS.put("auditorium", "University Auditorium");
        LOCATION_PATTERNS.put("academic", "Academic Block A");
    }

    @Override
    public AiDescriptionDtos.DescriptionAssistResponse assistDescription(String text) {
        AiDescriptionDtos.DescriptionAssistResponse response = new AiDescriptionDtos.DescriptionAssistResponse();
        if (text == null || text.isBlank()) {
            response.setStructuredDescription("No text provided.");
            return response;
        }

        String lower = text.toLowerCase();

        // 1. Detect Category
        String detectedCategory = "Other";
        for (Map.Entry<String, String> entry : CATEGORY_PATTERNS.entrySet()) {
            if (lower.contains(entry.getKey())) {
                detectedCategory = entry.getValue();
                break;
            }
        }
        response.setCategory(detectedCategory);

        // 2. Detect Color
        String detectedColor = "Unknown";
        for (String c : COLORS) {
            if (lower.contains(c)) {
                detectedColor = c.substring(0, 1).toUpperCase() + c.substring(1);
                break;
            }
        }
        response.setColor(detectedColor);

        // 3. Detect Brand
        String detectedBrand = null;
        if (lower.contains("nike")) detectedBrand = "Nike";
        else if (lower.contains("adidas")) detectedBrand = "Adidas";
        else if (lower.contains("apple")) detectedBrand = "Apple";
        else if (lower.contains("samsung")) detectedBrand = "Samsung";
        else if (lower.contains("dell")) detectedBrand = "Dell";
        else if (lower.contains("hydro flask") || lower.contains("hydroflask")) detectedBrand = "Hydro Flask";
        else if (lower.contains("casio")) detectedBrand = "Casio";
        else if (lower.contains("puma")) detectedBrand = "Puma";
        response.setBrand(detectedBrand);

        // 4. Detect Location
        String detectedLocation = "Central Library";
        for (Map.Entry<String, String> entry : LOCATION_PATTERNS.entrySet()) {
            if (lower.contains(entry.getKey())) {
                detectedLocation = entry.getValue();
                break;
            }
        }
        response.setSuggestedLocation(detectedLocation);

        // 5. Structure Description
        StringBuilder structured = new StringBuilder();
        if (!detectedColor.equals("Unknown")) structured.append(detectedColor).append(" ");
        if (detectedBrand != null) structured.append(detectedBrand).append(" ");
        structured.append(detectedCategory).append(" reported around ").append(detectedLocation).append(". ");
        structured.append("Original user note: \"").append(text.trim()).append("\"");
        response.setStructuredDescription(structured.toString());

        // 6. Keywords
        List<String> keywords = new ArrayList<>();
        keywords.add(detectedCategory);
        if (!detectedColor.equals("Unknown")) keywords.add(detectedColor);
        if (detectedBrand != null) keywords.add(detectedBrand);
        keywords.add(detectedLocation);
        response.setExtractedKeywords(keywords);

        // 7. Clarifying Questions
        List<String> clarifyingQuestions = new ArrayList<>();
        clarifyingQuestions.add("Did the item have any stickers, distinctive scratches, or keychains?");
        clarifyingQuestions.add("Were there any identifying contents or documents inside?");
        response.setClarifyingQuestions(clarifyingQuestions);

        return response;
    }
}
