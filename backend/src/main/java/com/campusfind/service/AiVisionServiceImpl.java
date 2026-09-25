package com.campusfind.service;

import com.campusfind.dto.AiVisionAnalysisResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class AiVisionServiceImpl implements AiVisionService {

    private static final Logger logger = LoggerFactory.getLogger(AiVisionServiceImpl.class);

    @Value("${campusfind.ai.provider:local}")
    private String provider;

    @Value("${campusfind.ai.api-key:}")
    private String apiKey;

    @Value("${campusfind.ai.model:gemini-1.5-flash}")
    private String modelName;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AiVisionServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public AiVisionAnalysisResponse analyzeImage(MultipartFile file, String contextHint) {
        logger.info("Analyzing image with configured provider: '{}', model: '{}'", provider, modelName);

        if (file == null || file.isEmpty()) {
            return createUnavailableResponse("No image file provided for analysis.");
        }

        // 1. Verify image is decodable & assess basic image quality
        BufferedImage image;
        byte[] imageBytes;
        try (InputStream is = file.getInputStream()) {
            imageBytes = file.getBytes();
            image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return createUnavailableResponse("Unable to decode uploaded image. Please upload a standard JPEG, PNG, or WEBP file.");
            }
        } catch (Exception e) {
            logger.warn("Failed to read image stream: {}", e.getMessage());
            return createUnavailableResponse("Error processing uploaded image. Please enter details manually.");
        }

        AiVisionAnalysisResponse.ImageQualityAssessment localQuality = assessBasicQuality(image);

        // 2. Dispatch to Real AI Multimodal Provider
        if ("gemini".equalsIgnoreCase(provider)) {
            if (apiKey == null || apiKey.isBlank()) {
                logger.info("Gemini provider selected but AI_API_KEY is not set. Returning graceful fallback.");
                return createManualFallbackResponse("AI assistance is temporarily unavailable (Gemini API key not configured). Please enter details manually.", localQuality);
            }
            return callGeminiVision(imageBytes, file.getContentType(), contextHint, localQuality);
        } else if ("openai".equalsIgnoreCase(provider)) {
            if (apiKey == null || apiKey.isBlank()) {
                logger.info("OpenAI provider selected but AI_API_KEY is not set. Returning graceful fallback.");
                return createManualFallbackResponse("AI assistance is temporarily unavailable (OpenAI API key not configured). Please enter details manually.", localQuality);
            }
            return callOpenAiVision(imageBytes, file.getContentType(), contextHint, localQuality);
        } else {
            // Local / Offline mode: Do not return fake AI results pretending to be computer vision
            logger.info("Local/offline mode active. Prompting user for manual verification.");
            return createManualFallbackResponse("AI assistance is in offline mode. Please verify or enter item details manually.", localQuality);
        }
    }

    private AiVisionAnalysisResponse callGeminiVision(byte[] imageBytes, String mimeType, String contextHint, AiVisionAnalysisResponse.ImageQualityAssessment localQuality) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String safeMime = (mimeType != null && mimeType.contains("png")) ? "image/png" : "image/jpeg";

            String prompt = "You are the vision AI for CampusFind, a university smart Lost & Found platform. " +
                    "Inspect this photograph of a found campus item carefully. " +
                    "Return ONLY a valid JSON object matching the exact schema below. Distinguish strictly between OBSERVED facts (colors, markings, visible damage, brand logos) and INFERRED possibilities (hypothetical contents, campus locations). " +
                    (contextHint != null && !contextHint.isBlank() ? "Context hint provided by finder: " + contextHint + ". " : "") +
                    "Schema:\n" +
                    "{\n" +
                    "  \"category\": \"Item category (one of: Bags, Electronics, Phones, Wallets, ID Cards, Bottles, Keys, Umbrellas, Books, Clothing, Other)\",\n" +
                    "  \"subcategory\": \"Specific subcategory (e.g. Laptop Backpack, Graphing Calculator, Insulated Tumbler)\",\n" +
                    "  \"color\": \"Primary dominant color name\",\n" +
                    "  \"brand\": \"Identifiable brand or logo name, or null\",\n" +
                    "  \"material\": \"Observed physical material (e.g. Nylon Fabric, Stainless Steel, Leather, Laminated Plastic)\",\n" +
                    "  \"visibleText\": \"Any visible text, numbers, or engravings, or null\",\n" +
                    "  \"distinctiveFeatures\": \"Specific visible features (stickers, scratches, zipper pulls, keychains)\",\n" +
                    "  \"observedFeatures\": [\"Directly observed feature 1\", \"Directly observed feature 2\"],\n" +
                    "  \"inferredFeatures\": [\"Inferred feature 1\", \"Inferred feature 2\"],\n" +
                    "  \"confidenceScore\": 0.92,\n" +
                    "  \"confidenceLevel\": \"High confidence\" | \"Medium confidence\" | \"Low confidence\",\n" +
                    "  \"verificationQuestions\": [\"Question 1 for claimant regarding hidden private attributes\", \"Question 2\"],\n" +
                    "  \"locationSuggestion\": \"Campus building name where this item is typically found (e.g. Central Library, Student Cafeteria, Academic Block A, Sports Complex)\",\n" +
                    "  \"locationReason\": \"Brief justification for location suggestion\",\n" +
                    "  \"imageQuality\": {\n" +
                    "    \"adequate\": true,\n" +
                    "    \"lightingCondition\": \"Good\",\n" +
                    "    \"clarity\": \"Sharp\",\n" +
                    "    \"suggestion\": \"Image clarity is adequate\"\n" +
                    "  }\n" +
                    "}";

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> inlineData = Map.of("mimeType", safeMime, "data", base64Image);
            Map<String, Object> imagePart = Map.of("inlineData", inlineData);

            Map<String, Object> content = Map.of("parts", List.of(textPart, imagePart));
            Map<String, Object> generationConfig = Map.of(
                    "temperature", 0.1,
                    "responseMimeType", "application/json"
            );
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(content),
                    "generationConfig", generationConfig
            );

            String requestJson = objectMapper.writeValueAsString(requestBody);
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode candidateText = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                if (!candidateText.isMissingNode()) {
                    String jsonText = cleanJsonResponse(candidateText.asText());
                    AiVisionAnalysisResponse result = objectMapper.readValue(jsonText, AiVisionAnalysisResponse.class);
                    result.setAiAvailable(true);
                    result.setAiMessage("AI multimodal vision analysis complete.");
                    if (result.getImageQuality() == null) {
                        result.setImageQuality(localQuality);
                    }
                    return result;
                }
            } else {
                logger.warn("Gemini API returned error code {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            logger.warn("Gemini vision analysis failed: {}", e.getMessage());
        }

        return createManualFallbackResponse("AI assistance is temporarily unavailable. Please enter details manually.", localQuality);
    }

    private AiVisionAnalysisResponse callOpenAiVision(byte[] imageBytes, String mimeType, String contextHint, AiVisionAnalysisResponse.ImageQualityAssessment localQuality) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String safeMime = (mimeType != null && mimeType.contains("png")) ? "image/png" : "image/jpeg";
            String dataUrl = "data:" + safeMime + ";base64," + base64Image;

            String prompt = "You are the vision AI for CampusFind. Inspect this campus lost/found item photograph. " +
                    "Return ONLY a JSON object matching this schema:\n" +
                    "{\"category\":\"...\",\"subcategory\":\"...\",\"color\":\"...\",\"brand\":\"...\",\"material\":\"...\",\"visibleText\":\"...\",\"distinctiveFeatures\":\"...\",\"observedFeatures\":[\"...\"],\"inferredFeatures\":[\"...\"],\"confidenceScore\":0.90,\"confidenceLevel\":\"High confidence\",\"verificationQuestions\":[\"...\"],\"locationSuggestion\":\"...\",\"locationReason\":\"...\",\"imageQuality\":{\"adequate\":true,\"lightingCondition\":\"Good\",\"clarity\":\"Sharp\",\"suggestion\":\"Clear\"}}";

            Map<String, Object> textMessage = Map.of("type", "text", "text", prompt);
            Map<String, Object> imageMessage = Map.of("type", "image_url", "image_url", Map.of("url", dataUrl));

            Map<String, Object> userMessage = Map.of("role", "user", "content", List.of(textMessage, imageMessage));
            Map<String, Object> payload = Map.of(
                    "model", modelName != null && !modelName.isBlank() ? modelName : "gpt-4o-mini",
                    "messages", List.of(userMessage),
                    "temperature", 0.1,
                    "response_format", Map.of("type", "json_object")
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                String contentJson = root.path("choices").path(0).path("message").path("content").asText();
                if (contentJson != null && !contentJson.isBlank()) {
                    AiVisionAnalysisResponse result = objectMapper.readValue(cleanJsonResponse(contentJson), AiVisionAnalysisResponse.class);
                    result.setAiAvailable(true);
                    result.setAiMessage("AI multimodal vision analysis complete.");
                    if (result.getImageQuality() == null) {
                        result.setImageQuality(localQuality);
                    }
                    return result;
                }
            } else {
                logger.warn("OpenAI API returned error code {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            logger.warn("OpenAI vision analysis failed: {}", e.getMessage());
        }

        return createManualFallbackResponse("AI assistance is temporarily unavailable. Please enter details manually.", localQuality);
    }

    private AiVisionAnalysisResponse.ImageQualityAssessment assessBasicQuality(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        long totalBrightness = 0;
        int sampleCount = 0;

        for (int y = 0; y < height; y += Math.max(1, height / 30)) {
            for (int x = 0; x < width; x += Math.max(1, width / 30)) {
                int rgb = image.getRGB(x, y);
                Color c = new Color(rgb);
                totalBrightness += (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                sampleCount++;
            }
        }

        int avgBrightness = sampleCount > 0 ? (int) (totalBrightness / sampleCount) : 128;
        AiVisionAnalysisResponse.ImageQualityAssessment quality = new AiVisionAnalysisResponse.ImageQualityAssessment();

        if (avgBrightness < 45) {
            quality.setAdequate(false);
            quality.setLightingCondition("Low Light / Underexposed");
            quality.setClarity("Dim");
            quality.setSuggestion("Image is dark. Consider taking a photo in better campus lighting.");
        } else if (avgBrightness > 230) {
            quality.setAdequate(true);
            quality.setLightingCondition("High Brightness / Overexposed");
            quality.setClarity("Acceptable");
            quality.setSuggestion("Noticeable glare detected. Key item features remain distinguishable.");
        } else {
            quality.setAdequate(true);
            quality.setLightingCondition("Optimal Campus Lighting");
            quality.setClarity("Crisp & Clear");
            quality.setSuggestion("Image clarity is sufficient for item identification.");
        }

        return quality;
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

    private AiVisionAnalysisResponse createUnavailableResponse(String message) {
        AiVisionAnalysisResponse resp = new AiVisionAnalysisResponse();
        resp.setAiAvailable(false);
        resp.setAiMessage(message);
        resp.setCategory("Other");
        resp.setColor("Unknown");
        resp.setConfidenceScore(null);
        resp.setConfidenceLevel("Unavailable");
        resp.setObservedFeatures(Collections.emptyList());
        resp.setInferredFeatures(Collections.emptyList());
        resp.setVerificationQuestions(Arrays.asList(
                "Please describe distinguishing markings, serial numbers, or stickers.",
                "What was inside or attached to the item?"
        ));
        resp.setImageQuality(new AiVisionAnalysisResponse.ImageQualityAssessment(false, "Unverified", "Unverified", message));
        return resp;
    }

    private AiVisionAnalysisResponse createManualFallbackResponse(String message, AiVisionAnalysisResponse.ImageQualityAssessment quality) {
        AiVisionAnalysisResponse resp = new AiVisionAnalysisResponse();
        resp.setAiAvailable(false);
        resp.setAiMessage(message);
        resp.setCategory("");
        resp.setColor("");
        resp.setBrand("");
        resp.setMaterial("");
        resp.setDistinctiveFeatures("");
        resp.setConfidenceScore(null);
        resp.setConfidenceLevel("Manual Entry");
        resp.setObservedFeatures(Collections.singletonList("Image uploaded and stored safely."));
        resp.setInferredFeatures(Collections.singletonList("Please confirm item details manually."));
        resp.setVerificationQuestions(Arrays.asList(
                "What unique items, papers, or marks are inside or on the item?",
                "Are there any specific scratches or attachments?"
        ));
        resp.setImageQuality(quality);
        return resp;
    }
}
