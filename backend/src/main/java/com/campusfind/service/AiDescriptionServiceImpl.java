package com.campusfind.service;

import com.campusfind.dto.AiDescriptionDtos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class AiDescriptionServiceImpl implements AiDescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(AiDescriptionServiceImpl.class);

    @Value("${campusfind.ai.provider:local}")
    private String provider;

    @Value("${campusfind.ai.api-key:}")
    private String apiKey;

    @Value("${campusfind.ai.model:gemini-1.5-flash}")
    private String modelName;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AiDescriptionServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public AiDescriptionDtos.DescriptionAssistResponse assistDescription(String text) {
        if (text == null || text.isBlank()) {
            AiDescriptionDtos.DescriptionAssistResponse emptyResp = new AiDescriptionDtos.DescriptionAssistResponse();
            emptyResp.setAiAvailable(true);
            emptyResp.setStructuredDescription("Please provide a description of the item you lost.");
            return emptyResp;
        }

        logger.info("Structuring description with provider: '{}'", provider);

        if ("gemini".equalsIgnoreCase(provider) && apiKey != null && !apiKey.isBlank()) {
            AiDescriptionDtos.DescriptionAssistResponse aiResp = callGeminiDescription(text);
            if (aiResp != null) return aiResp;
        } else if ("openai".equalsIgnoreCase(provider) && apiKey != null && !apiKey.isBlank()) {
            AiDescriptionDtos.DescriptionAssistResponse aiResp = callOpenAiDescription(text);
            if (aiResp != null) return aiResp;
        }

        // Graceful fallback: local token extractor for basic structure without pretending to be LLM
        return createFallbackResponse(text);
    }

    private AiDescriptionDtos.DescriptionAssistResponse callGeminiDescription(String text) {
        try {
            String prompt = "You are an AI assistant for CampusFind, a university lost and found system. " +
                    "Extract structured lost item details from this student's report: \"" + text + "\". " +
                    "Return ONLY a JSON object with this schema (no markdown, no backticks):\n" +
                    "{\n" +
                    "  \"category\": \"Item category (Bags, Electronics, Phones, Wallets, ID Cards, Bottles, Keys, Umbrellas, Books, Clothing, Other)\",\n" +
                    "  \"color\": \"Primary color or null\",\n" +
                    "  \"brand\": \"Brand name or null\",\n" +
                    "  \"suggestedLocation\": \"Campus location mentioned or null (e.g. Central Library, Student Cafeteria, Academic Block A, Sports Complex)\",\n" +
                    "  \"structuredDescription\": \"Clean formatted summary of the item and circumstances\",\n" +
                    "  \"extractedKeywords\": [\"keyword1\", \"keyword2\"],\n" +
                    "  \"clarifyingQuestions\": [\"Question 1 to ask student to narrow down item\", \"Question 2\"]\n" +
                    "}";

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                    "generationConfig", Map.of("temperature", 0.1, "responseMimeType", "application/json")
            );

            String requestJson = objectMapper.writeValueAsString(requestBody);
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                if (!textNode.isMissingNode()) {
                    String cleanJson = cleanJsonResponse(textNode.asText());
                    AiDescriptionDtos.DescriptionAssistResponse res = objectMapper.readValue(cleanJson, AiDescriptionDtos.DescriptionAssistResponse.class);
                    res.setAiAvailable(true);
                    res.setAiMessage("AI successfully structured your report.");
                    return res;
                }
            } else {
                logger.warn("Gemini description API error {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            logger.warn("Gemini description processing error: {}", e.getMessage());
        }
        return null;
    }

    private AiDescriptionDtos.DescriptionAssistResponse callOpenAiDescription(String text) {
        try {
            String prompt = "Extract structured lost item details from: \"" + text + "\". Return JSON conforming to: " +
                    "{\"category\":\"...\",\"color\":\"...\",\"brand\":\"...\",\"suggestedLocation\":\"...\",\"structuredDescription\":\"...\",\"extractedKeywords\":[\"...\"],\"clarifyingQuestions\":[\"...\"]}";

            Map<String, Object> payload = Map.of(
                    "model", modelName != null && !modelName.isBlank() ? modelName : "gpt-4o-mini",
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "temperature", 0.1,
                    "response_format", Map.of("type", "json_object")
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                String content = root.path("choices").path(0).path("message").path("content").asText();
                if (content != null && !content.isBlank()) {
                    AiDescriptionDtos.DescriptionAssistResponse res = objectMapper.readValue(cleanJsonResponse(content), AiDescriptionDtos.DescriptionAssistResponse.class);
                    res.setAiAvailable(true);
                    res.setAiMessage("AI successfully structured your report.");
                    return res;
                }
            }
        } catch (Exception e) {
            logger.warn("OpenAI description processing error: {}", e.getMessage());
        }
        return null;
    }

    private AiDescriptionDtos.DescriptionAssistResponse createFallbackResponse(String text) {
        AiDescriptionDtos.DescriptionAssistResponse response = new AiDescriptionDtos.DescriptionAssistResponse();
        response.setAiAvailable(false);
        response.setAiMessage("AI structuring is in local mode. Please review and refine the fields below.");

        String lower = text.toLowerCase();

        // Basic heuristic detection for graceful offline usability
        String detectedCategory = "Other";
        if (lower.contains("backpack") || lower.contains("bag")) detectedCategory = "Bags";
        else if (lower.contains("laptop") || lower.contains("charger") || lower.contains("airpod") || lower.contains("headphone") || lower.contains("calculator")) detectedCategory = "Electronics";
        else if (lower.contains("phone") || lower.contains("iphone") || lower.contains("mobile")) detectedCategory = "Phones";
        else if (lower.contains("wallet") || lower.contains("purse")) detectedCategory = "Wallets";
        else if (lower.contains("id card") || lower.contains("badge") || lower.contains("id")) detectedCategory = "ID Cards";
        else if (lower.contains("bottle") || lower.contains("flask")) detectedCategory = "Bottles";
        else if (lower.contains("key")) detectedCategory = "Keys";
        else if (lower.contains("umbrella")) detectedCategory = "Umbrellas";
        else if (lower.contains("book") || lower.contains("notebook")) detectedCategory = "Books";
        else if (lower.contains("jacket") || lower.contains("hoodie")) detectedCategory = "Clothing";
        response.setCategory(detectedCategory);

        String[] colors = {"black", "blue", "red", "white", "silver", "grey", "gray", "green", "yellow", "brown", "navy", "pink", "purple"};
        for (String c : colors) {
            if (lower.contains(c)) {
                response.setColor(c.substring(0, 1).toUpperCase() + c.substring(1));
                break;
            }
        }

        String[] brands = {"Nike", "Adidas", "Apple", "Samsung", "Dell", "Hydro Flask", "Casio", "Puma", "Fossil"};
        for (String b : brands) {
            if (lower.contains(b.toLowerCase())) {
                response.setBrand(b);
                break;
            }
        }

        if (lower.contains("library")) response.setSuggestedLocation("Central Library");
        else if (lower.contains("cafeteria") || lower.contains("canteen")) response.setSuggestedLocation("Student Cafeteria");
        else if (lower.contains("gym") || lower.contains("sports")) response.setSuggestedLocation("Sports Complex");
        else if (lower.contains("hostel") || lower.contains("dorm")) response.setSuggestedLocation("Hostel Block A");
        else if (lower.contains("lab")) response.setSuggestedLocation("Computer Science Lab Block");
        else if (lower.contains("gate") || lower.contains("bus")) response.setSuggestedLocation("Campus Main Gate");
        else if (lower.contains("academic") || lower.contains("block a")) response.setSuggestedLocation("Academic Block A");

        response.setStructuredDescription(text.trim());

        List<String> keywords = new ArrayList<>();
        keywords.add(detectedCategory);
        if (response.getColor() != null) keywords.add(response.getColor());
        if (response.getBrand() != null) keywords.add(response.getBrand());
        if (response.getSuggestedLocation() != null) keywords.add(response.getSuggestedLocation());
        response.setExtractedKeywords(keywords);

        response.setClarifyingQuestions(Arrays.asList(
                "Are there any stickers, keychains, or specific marks on your item?",
                "What was inside the pockets or compartments?"
        ));

        return response;
    }

    private String cleanJsonResponse(String raw) {
        String trimmed = raw.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }
}
