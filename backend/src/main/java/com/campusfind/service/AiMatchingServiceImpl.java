package com.campusfind.service;

import com.campusfind.dto.MatchCandidateDto;
import com.campusfind.entity.*;
import com.campusfind.repository.FoundReportRepository;
import com.campusfind.repository.LostReportRepository;
import com.campusfind.repository.MatchCandidateRepository;
import com.campusfind.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiMatchingServiceImpl implements AiMatchingService {

    private static final Logger logger = LoggerFactory.getLogger(AiMatchingServiceImpl.class);

    private final LostReportRepository lostReportRepository;
    private final FoundReportRepository foundReportRepository;
    private final MatchCandidateRepository matchCandidateRepository;
    private final NotificationRepository notificationRepository;

    public AiMatchingServiceImpl(
            LostReportRepository lostReportRepository,
            FoundReportRepository foundReportRepository,
            MatchCandidateRepository matchCandidateRepository,
            NotificationRepository notificationRepository) {
        this.lostReportRepository = lostReportRepository;
        this.foundReportRepository = foundReportRepository;
        this.matchCandidateRepository = matchCandidateRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public List<MatchCandidateDto> matchLostReport(LostReport lostReport) {
        List<FoundReport> activeFoundReports = foundReportRepository.findActiveReports();
        List<MatchCandidateDto> results = new ArrayList<>();

        for (FoundReport found : activeFoundReports) {
            MatchCandidate candidate = computeMatch(lostReport, found);
            if (candidate.getOverallScore() >= 50) {
                persistAndNotify(candidate);
                results.add(convertToDto(candidate));
            }
        }

        results.sort((a, b) -> Integer.compare(b.getOverallScore(), a.getOverallScore()));
        return results;
    }

    @Override
    @Transactional
    public List<MatchCandidateDto> matchFoundReport(FoundReport foundReport) {
        List<LostReport> activeLostReports = lostReportRepository.findByStatusOrderByCreatedAtDesc("ACTIVE");
        List<MatchCandidateDto> results = new ArrayList<>();

        for (LostReport lost : activeLostReports) {
            MatchCandidate candidate = computeMatch(lost, foundReport);
            if (candidate.getOverallScore() >= 50) {
                persistAndNotify(candidate);
                results.add(convertToDto(candidate));
            }
        }

        results.sort((a, b) -> Integer.compare(b.getOverallScore(), a.getOverallScore()));
        return results;
    }

    @Override
    public MatchCandidate computeMatch(LostReport lost, FoundReport found) {
        List<String> reasons = new ArrayList<>();

        // 1. Category Score (Weight: 25%)
        int categoryScore = 0;
        if (isCategoryCompatible(lost.getCategory(), found.getCategory())) {
            categoryScore = 100;
            reasons.add("✓ Same Category: " + found.getCategory());
        }

        // 2. Color Score (Weight: 15%)
        int colorScore = computeColorScore(lost.getColor(), found.getColor(), reasons);

        // 3. Brand Score (Weight: 15%)
        int brandScore = computeBrandScore(lost.getBrand(), found.getBrand(), reasons);

        // 4. Location Proximity Score (Weight: 15%)
        int locationScore = computeLocationScore(lost.getCampusLocation(), found.getCampusLocation(), reasons);

        // 5. Time Proximity Score (Weight: 10%)
        int timeScore = computeTimeScore(lost.getLostDate(), found.getFoundDate(), reasons);

        // 6. Semantic & Description Similarity Score (Weight: 20%)
        int semanticScore = computeSemanticSimilarity(
                lost.getTitle() + " " + lost.getDescription() + " " + (lost.getDistinctiveFeatures() != null ? lost.getDistinctiveFeatures() : ""),
                found.getTitle() + " " + found.getPublicDescription() + " " + (found.getDistinctiveFeatures() != null ? found.getDistinctiveFeatures() : ""),
                reasons
        );

        // Composite Weighted Score
        double composite = (categoryScore * 0.25)
                + (colorScore * 0.15)
                + (brandScore * 0.15)
                + (locationScore * 0.15)
                + (timeScore * 0.10)
                + (semanticScore * 0.20);

        int overallScore = (int) Math.round(composite);

        MatchCandidate candidate = new MatchCandidate();
        candidate.setLostReport(lost);
        candidate.setFoundReport(found);
        candidate.setOverallScore(overallScore);
        candidate.setCategoryScore(categoryScore);
        candidate.setColorScore(colorScore);
        candidate.setBrandScore(brandScore);
        candidate.setLocationScore(locationScore);
        candidate.setTimeScore(timeScore);
        candidate.setSemanticScore(semanticScore);
        candidate.setMatchReasons(String.join("; ", reasons));
        candidate.setStatus("PROPOSED");

        return candidate;
    }

    private boolean isCategoryCompatible(String cat1, String cat2) {
        if (cat1 == null || cat2 == null) return false;
        String c1 = cat1.trim().toLowerCase();
        String c2 = cat2.trim().toLowerCase();
        if (c1.equals(c2)) return true;
        if ((c1.contains("bag") || c1.contains("pack")) && (c2.contains("bag") || c2.contains("pack"))) return true;
        if ((c1.contains("phone") || c1.contains("mobile")) && (c2.contains("phone") || c2.contains("mobile"))) return true;
        if (c1.contains("wallet") && c2.contains("wallet")) return true;
        if (c1.contains("bottle") && c2.contains("bottle")) return true;
        if (c1.contains("card") && c2.contains("card")) return true;
        if (c1.contains("key") && c2.contains("key")) return true;
        return false;
    }

    private int computeColorScore(String c1, String c2, List<String> reasons) {
        if (c1 == null || c2 == null || c1.equalsIgnoreCase("Unknown") || c2.equalsIgnoreCase("Unknown")) {
            return 50; // Neutral if unspecified
        }
        String color1 = c1.trim().toLowerCase();
        String color2 = c2.trim().toLowerCase();

        if (color1.equals(color2) || color1.contains(color2) || color2.contains(color1)) {
            reasons.add("✓ Matching Color: " + c2);
            return 100;
        }
        // Similar tones
        if ((color1.contains("black") && color2.contains("grey")) || (color1.contains("grey") && color2.contains("black"))) {
            reasons.add("~ Compatible Dark Tones (" + c1 + " / " + c2 + ")");
            return 70;
        }
        if ((color1.contains("blue") && color2.contains("navy")) || (color1.contains("navy") && color2.contains("blue"))) {
            reasons.add("✓ Compatible Blue Tones (" + c1 + " / " + c2 + ")");
            return 90;
        }
        return 20;
    }

    private int computeBrandScore(String b1, String b2, List<String> reasons) {
        if (b1 == null || b2 == null || b1.isBlank() || b2.isBlank()) {
            return 50; // Neutral if brand unknown
        }
        String brand1 = b1.trim().toLowerCase();
        String brand2 = b2.trim().toLowerCase();

        if (brand1.equals(brand2) || brand1.contains(brand2) || brand2.contains(brand1)) {
            reasons.add("✓ Matching Brand: " + b2);
            return 100;
        }
        return 10;
    }

    private int computeLocationScore(CampusLocation loc1, CampusLocation loc2, List<String> reasons) {
        if (loc1 == null || loc2 == null) {
            return 50;
        }
        if (loc1.getId().equals(loc2.getId())) {
            reasons.add("✓ Same Location: " + loc2.getName());
            return 100;
        }
        if (loc1.getZone() != null && loc1.getZone().equalsIgnoreCase(loc2.getZone())) {
            reasons.add("~ Same Campus Zone: " + loc2.getZone());
            return 75;
        }
        return 35;
    }

    private int computeTimeScore(java.time.LocalDateTime t1, java.time.LocalDateTime t2, List<String> reasons) {
        if (t1 == null || t2 == null) {
            return 50;
        }
        long diffHours = Math.abs(Duration.between(t1, t2).toHours());
        if (diffHours <= 24) {
            reasons.add("✓ Found within 24 hours of loss");
            return 100;
        } else if (diffHours <= 72) {
            reasons.add("✓ Found within 3 days");
            return 80;
        } else if (diffHours <= 168) {
            reasons.add("~ Reported within same week");
            return 60;
        }
        return 30;
    }

    private int computeSemanticSimilarity(String text1, String text2, List<String> reasons) {
        if (text1 == null || text2 == null) return 40;

        Set<String> words1 = extractSignificantWords(text1);
        Set<String> words2 = extractSignificantWords(text2);

        if (words1.isEmpty() || words2.isEmpty()) return 50;

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        double jaccard = (double) intersection.size() / union.size();
        int score = (int) Math.min(100, Math.round(jaccard * 180)); // Scaled Jaccard

        if (score >= 60) {
            reasons.add("✓ Strong description & feature correlation");
        }
        return Math.max(30, score);
    }

    private Set<String> extractSignificantWords(String text) {
        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "a", "an", "the", "in", "on", "at", "to", "for", "with", "and", "or", "of", "my", "is", "was", "it", "near", "around"
        ));
        return Arrays.stream(text.toLowerCase().split("[^a-zA-Z0-9]+"))
                .filter(w -> w.length() > 2 && !stopWords.contains(w))
                .collect(Collectors.toSet());
    }

    private void persistAndNotify(MatchCandidate candidate) {
        Optional<MatchCandidate> existing = matchCandidateRepository.findByLostReportIdAndFoundReportId(
                candidate.getLostReport().getId(),
                candidate.getFoundReport().getId()
        );

        MatchCandidate toSave = candidate;
        if (existing.isPresent()) {
            toSave = existing.get();
            toSave.setOverallScore(candidate.getOverallScore());
            toSave.setCategoryScore(candidate.getCategoryScore());
            toSave.setColorScore(candidate.getColorScore());
            toSave.setBrandScore(candidate.getBrandScore());
            toSave.setLocationScore(candidate.getLocationScore());
            toSave.setTimeScore(candidate.getTimeScore());
            toSave.setSemanticScore(candidate.getSemanticScore());
            toSave.setMatchReasons(candidate.getMatchReasons());
        }

        matchCandidateRepository.save(toSave);

        // Notify lost report owner if high confidence match
        if (candidate.getOverallScore() >= 70) {
            Notification notification = new Notification(
                    candidate.getLostReport().getOwner(),
                    "Potential Match Found (" + candidate.getOverallScore() + "%)",
                    "A found item (" + candidate.getFoundReport().getTitle() + ") matches your lost report " + candidate.getLostReport().getReferenceId(),
                    "MATCH_FOUND",
                    "/matches"
            );
            notificationRepository.save(notification);
        }
    }

    private MatchCandidateDto convertToDto(MatchCandidate c) {
        MatchCandidateDto dto = new MatchCandidateDto();
        dto.setId(c.getId());
        dto.setLostReportId(c.getLostReport().getId());
        dto.setLostReferenceId(c.getLostReport().getReferenceId());
        dto.setLostTitle(c.getLostReport().getTitle());
        dto.setLostCategory(c.getLostReport().getCategory());
        dto.setLostLocation(c.getLostReport().getCampusLocation() != null ? c.getLostReport().getCampusLocation().getName() : "Campus");
        dto.setLostDate(c.getLostReport().getLostDate());
        dto.setLostImageUrl(c.getLostReport().getReferenceImageUrl());

        dto.setFoundReportId(c.getFoundReport().getId());
        dto.setFoundReferenceId(c.getFoundReport().getReferenceId());
        dto.setFoundTitle(c.getFoundReport().getTitle());
        dto.setFoundCategory(c.getFoundReport().getCategory());
        dto.setFoundLocation(c.getFoundReport().getCampusLocation() != null ? c.getFoundReport().getCampusLocation().getName() : "Campus");
        dto.setFoundDate(c.getFoundReport().getFoundDate());
        dto.setFoundImageUrl(c.getFoundReport().getPrimaryImageUrl());

        dto.setOverallScore(c.getOverallScore());
        dto.setCategoryScore(c.getCategoryScore());
        dto.setColorScore(c.getColorScore());
        dto.setBrandScore(c.getBrandScore());
        dto.setLocationScore(c.getLocationScore());
        dto.setTimeScore(c.getTimeScore());
        dto.setSemanticScore(c.getSemanticScore());

        if (c.getMatchReasons() != null) {
            dto.setMatchReasons(Arrays.asList(c.getMatchReasons().split("; ")));
        } else {
            dto.setMatchReasons(Collections.emptyList());
        }
        dto.setStatus(c.getStatus());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
