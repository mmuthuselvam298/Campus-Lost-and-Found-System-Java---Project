package com.campusfind.service;

import com.campusfind.dto.AiVisionAnalysisResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AiVisionService {

    /**
     * Analyzes an uploaded photograph of a lost or found item.
     * Extracts observed vs inferred features, category, color, material,
     * text OCR hints, and suggests anti-fraud verification questions.
     */
    AiVisionAnalysisResponse analyzeImage(MultipartFile file, String contextHint);
}
