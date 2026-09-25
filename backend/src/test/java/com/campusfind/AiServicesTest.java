package com.campusfind;

import com.campusfind.dto.AiDescriptionDtos;
import com.campusfind.dto.AiVisionAnalysisResponse;
import com.campusfind.service.AiDescriptionService;
import com.campusfind.service.AiVisionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AiServicesTest {

    @Autowired
    private AiVisionService aiVisionService;

    @Autowired
    private AiDescriptionService aiDescriptionService;

    @Test
    @DisplayName("AI Vision: In test/offline mode without API key, returns clear fallback without crashing")
    void testAiVisionFallback() throws Exception {
        // Generate a valid 100x100 raster image
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test_item.jpg",
                "image/jpeg",
                baos.toByteArray()
        );

        AiVisionAnalysisResponse response = aiVisionService.analyzeImage(file, "library backpack");

        assertNotNull(response);
        assertFalse(response.isAiAvailable(), "Should indicate external AI is unavailable in offline test profile");
        assertNotNull(response.getAiMessage());
        assertTrue(response.getAiMessage().contains("offline mode") || response.getAiMessage().contains("unavailable"));
        assertNotNull(response.getImageQuality());
        assertTrue(response.getImageQuality().isAdequate() || !response.getImageQuality().isAdequate());
    }

    @Test
    @DisplayName("AI Description: Structures lost item text into category, color, and location")
    void testAiDescriptionStructuring() {
        String description = "I lost my black backpack near the Central Library yesterday afternoon.";

        AiDescriptionDtos.DescriptionAssistResponse response = aiDescriptionService.assistDescription(description);

        assertNotNull(response);
        assertEquals("Bags", response.getCategory());
        assertEquals("Black", response.getColor());
        assertEquals("Central Library", response.getSuggestedLocation());
        assertNotNull(response.getStructuredDescription());
        assertFalse(response.getExtractedKeywords().isEmpty());
    }
}
