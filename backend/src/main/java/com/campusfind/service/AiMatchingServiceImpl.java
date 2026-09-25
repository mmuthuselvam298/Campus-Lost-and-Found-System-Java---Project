package com.campusfind.service;

import com.campusfind.dto.MatchCandidateDto;
import com.campusfind.entity.*;
import com.campusfind.repository.FoundReportRepository;
import com.campusfind.repository.LostReportRepository;
import com.campusfind.repository.MatchCandidateRepository;
import com.campusfind.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${campusfind.matching.weights.category:0.25}")
    private double categoryWeight = 0.25;

    @Value("${campusfind.matching.weights.color:0.15}")
    private double colorWeight = 0.15;

    @Value("${campusfind.matching.weights.brand:0.15}")
    private double brandWeight = 0.15;

    @Value("${campusfind.matching.weights.location:0.15}")
    private double locationWeight = 0.15;

    @Value("${campusfind.matching.weights.time:0.10}")
    private double timeWeight = 0.10;

    @Value("${campusfind.matching.weights.semantic:0.20}")
    private double semanticWeight = 0.20;

    @Value("${campusfind.matching.weights.visual:0.00}")
    private double visualWeight = 0.00;

    @Value("${campusfind.matching.threshold:50}")
    private int matchThreshold = 50;

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
        // Step 1: Candidate Retrieval
        List<FoundReport> activeFoundReports = foundReportRepository.findActiveReports();
        List<MatchCandidateDto> results = new ArrayList<>();

        for (FoundReport found : activeFoundReports) {
            MatchCandidate candidate = computeMatch(lostReport, found);
            if (candidate.getOverallScore() >= matchThreshold) {
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
        // Step 1: Candidate Retrieval
        List<LostReport> activeLostReports = lostReportRepository.findByStatusOrderByCreatedAtDesc("ACTIVE");
        List<MatchCandidateDto> results = new ArrayList<>();

        for (LostReport lost : activeLostReports) {
            MatchCandidate candidate = computeMatch(lost, foundReport);
            if (candidate.getOverallScore() >= matchThreshold) {
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

        // 1. Structured Category Match
        int categoryScore = computeCategoryScore(lost.getCategory(), found.getCategory(), reasons);

        // 2. Color Compatibility
        int colorScore = computeColorScore(lost.getColor(), found.getColor(), reasons);

        // 3. Brand Match
        boolean brandAvailable = isAvailable(lost.getBrand()) && isAvailable(found.getBrand());
        int brandScore = brandAvailable ? computeBrandScore(lost.getBrand(), found.getBrand(), reasons) : 50;

        // 4. Campus Location Proximity
        int locationScore = computeLocationScore(lost.getCampusLocation(), found.getCampusLocation(), reasons);

        // 5. Time Compatibility
        int timeScore = computeTimeScore(lost.getLostDate(), found.getFoundDate(), reasons);

        // 6. Semantic Vector Similarity (NLP Cosine Vector Similarity)
        String lostFullText = buildReportText(lost.getTitle(), lost.getDescription(), lost.getDistinctiveFeatures());
        String foundFullText = buildReportText(found.getTitle(), found.getPublicDescription(), found.getDistinctiveFeatures());
        int semanticScore = computeSemanticSimilarity(lostFullText, foundFullText, reasons);

        // 7. Visual Similarity Signal
        int visualScore = computeVisualSignal(lost, found, reasons);

        // Dynamic Weight Normalization: distribute weight across active signals
        double totalActiveWeight = categoryWeight + colorWeight + (brandAvailable ? brandWeight : 0.0)
                + locationWeight + timeWeight + semanticWeight + (visualWeight > 0 ? visualWeight : 0.0);

        if (totalActiveWeight <= 0) totalActiveWeight = 1.0;

        double weightedSum = (categoryScore * categoryWeight)
                + (colorScore * colorWeight)
                + (brandAvailable ? (brandScore * brandWeight) : 0.0)
                + (locationScore * locationWeight)
                + (timeScore * timeWeight)
                + (semanticScore * semanticWeight)
                + (visualWeight > 0 ? (visualScore * visualWeight) : 0.0);

        int overallScore = (int) Math.round(weightedSum / totalActiveWeight);
        overallScore = Math.max(0, Math.min(100, overallScore));

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
        candidate.setVisualScore(visualScore);
        candidate.setMatchReasons(String.join("; ", reasons));
        candidate.setStatus("PROPOSED");

        return candidate;
    }

    private int computeCategoryScore(String cat1, String cat2, List<String> reasons) {
        if (cat1 == null || cat2 == null) return 0;
        String c1 = cat1.trim().toLowerCase();
        String c2 = cat2.trim().toLowerCase();

        if (c1.equals(c2)) {
            reasons.add("✓ Same Category: " + cat2);
            return 100;
        }
        if ((c1.contains("bag") || c1.contains("pack")) && (c2.contains("bag") || c2.contains("pack"))) {
            reasons.add("✓ Same Category: " + cat2);
            return 95;
        }
        if ((c1.contains("phone") || c1.contains("mobile")) && (c2.contains("phone") || c2.contains("mobile"))) {
            reasons.add("✓ Same Category: " + cat2);
            return 95;
        }
        if (c1.contains("wallet") && c2.contains("wallet")) {
            reasons.add("✓ Same Category: " + cat2);
            return 95;
        }
        if (c1.contains("bottle") && c2.contains("bottle")) {
            reasons.add("✓ Same Category: " + cat2);
            return 95;
        }
        if (c1.contains("card") && c2.contains("card")) {
            reasons.add("✓ Same Category: " + cat2);
            return 95;
        }
        if (c1.contains("key") && c2.contains("key")) {
            reasons.add("✓ Same Category: " + cat2);
            return 95;
        }
        return 0;
    }

    private int computeColorScore(String c1, String c2, List<String> reasons) {
        if (!isAvailable(c1) || !isAvailable(c2)) {
            return 50; // Neutral if unspecified
        }
        String color1 = c1.trim().toLowerCase();
        String color2 = c2.trim().toLowerCase();

        if (color1.equals(color2) || color1.contains(color2) || color2.contains(color1)) {
            reasons.add("✓ Matching Color: " + c2);
            return 100;
        }
        if ((color1.contains("black") && color2.contains("grey")) || (color1.contains("grey") && color2.contains("black"))) {
            reasons.add("~ Compatible dark tone (" + c1 + " / " + c2 + ")");
            return 75;
        }
        if ((color1.contains("blue") && color2.contains("navy")) || (color1.contains("navy") && color2.contains("blue"))) {
            reasons.add("✓ Compatible blue tone (" + c1 + " / " + c2 + ")");
            return 90;
        }
        return 20;
    }

    private int computeBrandScore(String b1, String b2, List<String> reasons) {
        String brand1 = b1.trim().toLowerCase();
        String brand2 = b2.trim().toLowerCase();

        if (brand1.equals(brand2) || brand1.contains(brand2) || brand2.contains(brand1)) {
            reasons.add("✓ Matching Brand: " + b2);
            return 100;
        }
        return 15;
    }


    private int computeLocationScore(CampusLocation loc1, CampusLocation loc2, List<String> reasons) {
        if (loc1 == null || loc2 == null) {
            return 50;
        }
        if (loc1.getId().equals(loc2.getId())) {
            reasons.add("✓ Same campus building: " + loc2.getName());
            return 100;
        }
        if (loc1.getZone() != null && loc1.getZone().equalsIgnoreCase(loc2.getZone())) {
            reasons.add("~ Same campus zone: " + loc2.getZone());
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
        if (text1.isBlank() || text2.isBlank()) return 40;

        Map<String, Double> v1 = buildTfVector(text1);
        Map<String, Double> v2 = buildTfVector(text2);

        double cosine = computeCosine(v1, v2);
        int score = (int) Math.round(cosine * 100.0);

        if (score >= 60) {
            reasons.add("✓ Strong description & feature correlation");
        } else if (score >= 40) {
            reasons.add("~ Moderate description overlap");
        }
        return Math.max(25, Math.min(100, score));
    }

    private int computeVisualSignal(LostReport lost, FoundReport found, List<String> reasons) {
        int visualMatch = 0;
        int signalsCount = 0;

        if (isAvailable(lost.getColor()) && isAvailable(found.getColor())) {
            signalsCount++;
            if (lost.getColor().equalsIgnoreCase(found.getColor())) {
                visualMatch += 40;
            }
        }

        if (isAvailable(lost.getDistinctiveFeatures()) && isAvailable(found.getDistinctiveFeatures())) {
            signalsCount++;
            Set<String> words1 = extractTokens(lost.getDistinctiveFeatures());
            Set<String> words2 = extractTokens(found.getDistinctiveFeatures());
            Set<String> intersection = new HashSet<>(words1);
            intersection.retainAll(words2);
            if (!intersection.isEmpty()) {
                visualMatch += 40;
                reasons.add("✓ Potential visual match (common distinct features: " + String.join(", ", intersection) + ")");
            }
        }

        if (isAvailable(found.getMaterial()) && lost.getDescription() != null && lost.getDescription().toLowerCase().contains(found.getMaterial().toLowerCase())) {
            visualMatch += 20;
            reasons.add("✓ Material compatibility: " + found.getMaterial());
        }

        return signalsCount > 0 ? Math.min(100, visualMatch) : 50;
    }

    private Map<String, Double> buildTfVector(String text) {
        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "a", "an", "the", "in", "on", "at", "to", "for", "with", "and", "or", "of", "my", "is", "was", "it", "near", "around", "have", "lost", "found"
        ));

        String[] tokens = text.toLowerCase().split("[^a-zA-Z0-9]+");
        Map<String, Integer> counts = new HashMap<>();

        for (String t : tokens) {
            if (t.length() > 2 && !stopWords.contains(t)) {
                counts.put(t, counts.getOrDefault(t, 0) + 1);
            }
        }

        Map<String, Double> vector = new HashMap<>();
        double sumSquares = 0.0;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            double tf = 1.0 + Math.log(e.getValue());
            vector.put(e.getKey(), tf);
            sumSquares += tf * tf;
        }

        double norm = Math.sqrt(sumSquares);
        if (norm > 0) {
            for (Map.Entry<String, Double> e : vector.entrySet()) {
                vector.put(e.getKey(), e.getValue() / norm);
            }
        }

        return vector;
    }

    private double computeCosine(Map<String, Double> v1, Map<String, Double> v2) {
        double dot = 0.0;
        for (Map.Entry<String, Double> e : v1.entrySet()) {
            if (v2.containsKey(e.getKey())) {
                dot += e.getValue() * v2.get(e.getKey());
            }
        }
        return dot;
    }

    private Set<String> extractTokens(String text) {
        Set<String> stopWords = new HashSet<>(Arrays.asList("the", "a", "an", "and", "in", "with", "it", "has", "inside", "there", "is", "was", "are"));
        return Arrays.stream(text.toLowerCase().split("[^a-zA-Z0-9]+"))
                .filter(w -> w.length() > 2 && !stopWords.contains(w))
                .collect(Collectors.toSet());
    }

    private String buildReportText(String title, String desc, String features) {
        return (title != null ? title : "") + " "
                + (desc != null ? desc : "") + " "
                + (features != null ? features : "");
    }

    private boolean isAvailable(String s) {
        return s != null && !s.isBlank() && !s.equalsIgnoreCase("unknown");
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
            toSave.setVisualScore(candidate.getVisualScore());
            toSave.setMatchReasons(candidate.getMatchReasons());
        }

        matchCandidateRepository.save(toSave);

        // Notify lost report owner if high confidence match
        if (candidate.getOverallScore() >= 70 && candidate.getLostReport().getOwner() != null) {
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
        dto.setVisualScore(c.getVisualScore());

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
