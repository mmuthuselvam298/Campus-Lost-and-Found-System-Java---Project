package com.campusfind.controller;

import com.campusfind.dto.AiDescriptionDtos;
import com.campusfind.dto.AiVisionAnalysisResponse;
import com.campusfind.service.AiDescriptionService;
import com.campusfind.service.AiVisionService;
import com.campusfind.service.StorageService;
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

    public AiController(
            AiVisionService aiVisionService,
            AiDescriptionService aiDescriptionService,
            StorageService storageService) {
        this.aiVisionService = aiVisionService;
        this.aiDescriptionService = aiDescriptionService;
        this.storageService = storageService;
    }

    @PostMapping("/analyze-image")
    public ResponseEntity<?> analyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "contextHint", required = false) String contextHint) {
        if (file.isEmpty()) {
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
    public ResponseEntity<AiDescriptionDtos.DescriptionAssistResponse> assistDescription(
            @RequestBody AiDescriptionDtos.DescriptionAssistRequest request) {
        AiDescriptionDtos.DescriptionAssistResponse response = aiDescriptionService.assistDescription(request.getText());
        return ResponseEntity.ok(response);
    }
}
