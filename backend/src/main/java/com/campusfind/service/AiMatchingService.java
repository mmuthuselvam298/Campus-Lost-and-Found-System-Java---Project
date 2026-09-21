package com.campusfind.service;

import com.campusfind.dto.MatchCandidateDto;
import com.campusfind.entity.FoundReport;
import com.campusfind.entity.LostReport;
import com.campusfind.entity.MatchCandidate;

import java.util.List;

public interface AiMatchingService {

    /**
     * Computes match candidates between a new/updated LostReport and all active FoundReports.
     */
    List<MatchCandidateDto> matchLostReport(LostReport lostReport);

    /**
     * Computes match candidates between a new/updated FoundReport and all active LostReports.
     */
    List<MatchCandidateDto> matchFoundReport(FoundReport foundReport);

    /**
     * Computes detailed multi-signal match score between a pair of reports.
     */
    MatchCandidate computeMatch(LostReport lost, FoundReport found);
}
