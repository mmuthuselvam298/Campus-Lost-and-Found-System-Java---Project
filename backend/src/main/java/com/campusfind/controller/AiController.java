package com.campusfind.controller;

import com.campusfind.dto.AiDescriptionDtos;
import com.campusfind.dto.AiVisionAnalysisResponse;
import com.campusfind.service.AiDescriptionService;
import com.campusfind.service.AiVisionService;
import com.campusfind.service.RateLimitingService;
import com.campusfind.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiVisionService aiVisionService;
    private final AiDescriptionService aiDescriptionService;
    private final StorageService storageService;
    private final RateLimitingService rateLimitingService;

    public AiController(
            AiVisionService aiVisionService,
            AiDescriptionService aiDescriptionService,
            StorageService storageService,
            RateLimitingService rateLimitingService) {
        this.aiVisionService = aiVisionService;
        this.aiDescriptionService = aiDescriptionService;
        this.storageService = storageService;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/analyze-image")
    public ResponseEntity<?> analyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "contextHint", required = false) String contextHint,
            HttpServletRequest request) {

        String clientIp = getClientIp(request);
        if (!rateLimitingService.allowAiRequest(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded for AI vision requests. Please wait a moment."));
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Image file is required"));
        }

        try {
            // First store the image securely
            StorageService.StorageResult storageResult = storageService.store(file);

            // Then run AI multimodal vision feature extraction
            AiVisionAnalysisResponse analysis = aiVisionService.analyzeImage(file, contextHint);

            Map<String, Object> result = new HashMap<>();
            result.put("analysis", analysis);
            result.put("imageUrl", storageResult.getFileUrl());
            result.put("thumbnailUrl", storageResult.getThumbnailUrl());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "AI vision analysis failed: " + e.getMessage()));
        }
    }

    @PostMapping("/assist-description")
    public ResponseEntity<?> assistDescription(
            @RequestBody AiDescriptionDtos.DescriptionAssistRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        if (!rateLimitingService.allowAiRequest(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded for AI description requests. Please wait a moment."));
        }

        AiDescriptionDtos.DescriptionAssistResponse response = aiDescriptionService.assistDescription(request.getText());
        return ResponseEntity.ok(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
