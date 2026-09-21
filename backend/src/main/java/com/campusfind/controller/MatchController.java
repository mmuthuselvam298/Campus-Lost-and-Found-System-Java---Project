package com.campusfind.controller;

import com.campusfind.dto.MatchCandidateDto;
import com.campusfind.entity.LostReport;
import com.campusfind.entity.MatchCandidate;
import com.campusfind.repository.LostReportRepository;
import com.campusfind.repository.MatchCandidateRepository;
import com.campusfind.security.UserPrincipal;
import com.campusfind.service.AiMatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchCandidateRepository matchCandidateRepository;
    private final LostReportRepository lostReportRepository;
    private final AiMatchingService aiMatchingService;

    public MatchController(
            MatchCandidateRepository matchCandidateRepository,
            LostReportRepository lostReportRepository,
            AiMatchingService aiMatchingService) {
        this.matchCandidateRepository = matchCandidateRepository;
        this.lostReportRepository = lostReportRepository;
        this.aiMatchingService = aiMatchingService;
    }

    @GetMapping("/lost/{lostReportId}")
    public ResponseEntity<List<MatchCandidateDto>> getMatchesForLostReport(@PathVariable("lostReportId") Long lostReportId) {
        List<MatchCandidate> candidates = matchCandidateRepository.findByLostReportIdOrderByOverallScoreDesc(lostReportId);
        if (candidates.isEmpty()) {
            // Compute on the fly if not yet indexed
            lostReportRepository.findById(lostReportId).ifPresent(aiMatchingService::matchLostReport);
            candidates = matchCandidateRepository.findByLostReportIdOrderByOverallScoreDesc(lostReportId);
        }
        return ResponseEntity.ok(candidates.stream().map(this::mapDto).collect(Collectors.toList()));
    }

    @GetMapping("/found/{foundReportId}")
    public ResponseEntity<List<MatchCandidateDto>> getMatchesForFoundReport(@PathVariable("foundReportId") Long foundReportId) {
        List<MatchCandidate> candidates = matchCandidateRepository.findByFoundReportIdOrderByOverallScoreDesc(foundReportId);
        return ResponseEntity.ok(candidates.stream().map(this::mapDto).collect(Collectors.toList()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<MatchCandidateDto>> getMyMatches(@AuthenticationPrincipal UserPrincipal currentUser) {
        Long userId = currentUser != null ? currentUser.getId() : 1L; // Fallback to demo student
        List<MatchCandidate> candidates = matchCandidateRepository.findByLostReportOwnerId(userId);
        return ResponseEntity.ok(candidates.stream().map(this::mapDto).collect(Collectors.toList()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MatchCandidateDto>> getAllMatches() {
        List<MatchCandidate> candidates = matchCandidateRepository.findAll()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getOverallScore(), a.getOverallScore()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(candidates.stream().map(this::mapDto).collect(Collectors.toList()));
    }

    private MatchCandidateDto mapDto(MatchCandidate c) {
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
