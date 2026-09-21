package com.campusfind;

import com.campusfind.entity.CampusLocation;
import com.campusfind.entity.FoundReport;
import com.campusfind.entity.LostReport;
import com.campusfind.entity.MatchCandidate;
import com.campusfind.repository.FoundReportRepository;
import com.campusfind.repository.LostReportRepository;
import com.campusfind.repository.MatchCandidateRepository;
import com.campusfind.repository.NotificationRepository;
import com.campusfind.service.AiMatchingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AiMatchingEngineTest {

    private LostReportRepository lostReportRepository;
    private FoundReportRepository foundReportRepository;
    private MatchCandidateRepository matchCandidateRepository;
    private NotificationRepository notificationRepository;
    private AiMatchingServiceImpl matchingService;

    @BeforeEach
    void setUp() {
        lostReportRepository = Mockito.mock(LostReportRepository.class);
        foundReportRepository = Mockito.mock(FoundReportRepository.class);
        matchCandidateRepository = Mockito.mock(MatchCandidateRepository.class);
        notificationRepository = Mockito.mock(NotificationRepository.class);

        matchingService = new AiMatchingServiceImpl(
                lostReportRepository,
                foundReportRepository,
                matchCandidateRepository,
                notificationRepository
        );
    }

    @Test
    @DisplayName("Should detect high-confidence match when category, color, brand, and location match")
    void testHighConfidenceMatch() {
        CampusLocation library = new CampusLocation("Central Library", "North Quad", "Main library", 4, "", 12.82, 80.04);
        library.setId(1L);

        LostReport lost = new LostReport();
        lost.setId(10L);
        lost.setReferenceId("LR-2026-001");
        lost.setTitle("Black Nike Backpack with Red Zipper Pull");
        lost.setCategory("Bags");
        lost.setColor("Black");
        lost.setBrand("Nike");
        lost.setDescription("Lost backpack containing laptop and red keychain pull");
        lost.setCampusLocation(library);
        lost.setLostDate(LocalDateTime.now().minusHours(5));

        FoundReport found = new FoundReport();
        found.setId(20L);
        found.setReferenceId("LF-2026-002");
        found.setTitle("Black Backpack Nike with Red Keychain");
        found.setCategory("Bags");
        found.setColor("Black");
        found.setBrand("Nike");
        found.setPublicDescription("Found black backpack in 2nd floor library reading cubicles with red pull");
        found.setCampusLocation(library);
        found.setFoundDate(LocalDateTime.now().minusHours(4));

        MatchCandidate match = matchingService.computeMatch(lost, found);

        assertNotNull(match);
        assertTrue(match.getOverallScore() >= 85, "Expected high confidence match score >= 85, got: " + match.getOverallScore());
        assertEquals(100, match.getCategoryScore());
        assertEquals(100, match.getColorScore());
        assertEquals(100, match.getBrandScore());
        assertEquals(100, match.getLocationScore());
        assertTrue(match.getMatchReasons().contains("Same Category"));
        assertTrue(match.getMatchReasons().contains("Matching Color"));
        assertTrue(match.getMatchReasons().contains("Matching Brand"));
    }

    @Test
    @DisplayName("Should produce low score when categories are completely incompatible")
    void testIncompatibleCategories() {
        CampusLocation loc1 = new CampusLocation("Central Library", "North", "", 1, "", 0.0, 0.0);
        loc1.setId(1L);
        CampusLocation loc2 = new CampusLocation("Sports Complex", "South", "", 1, "", 0.0, 0.0);
        loc2.setId(2L);

        LostReport lost = new LostReport();
        lost.setCategory("Wallets");
        lost.setColor("Brown");
        lost.setCampusLocation(loc1);
        lost.setLostDate(LocalDateTime.now().minusDays(10));
        lost.setTitle("Brown Leather Wallet");
        lost.setDescription("Lost wallet with cards");

        FoundReport found = new FoundReport();
        found.setCategory("Bottles");
        found.setColor("Blue");
        found.setCampusLocation(loc2);
        found.setFoundDate(LocalDateTime.now());
        found.setTitle("Blue Water Bottle");
        found.setPublicDescription("Found gym bottle");

        MatchCandidate match = matchingService.computeMatch(lost, found);

        assertNotNull(match);
        assertTrue(match.getOverallScore() < 40, "Expected low match score < 40, got: " + match.getOverallScore());
        assertEquals(0, match.getCategoryScore());
    }
}
