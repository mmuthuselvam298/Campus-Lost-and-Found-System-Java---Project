package com.campusfind.repository;

import com.campusfind.entity.MatchCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchCandidateRepository extends JpaRepository<MatchCandidate, Long> {

    List<MatchCandidate> findByLostReportIdOrderByOverallScoreDesc(Long lostReportId);

    List<MatchCandidate> findByFoundReportIdOrderByOverallScoreDesc(Long foundReportId);

    @Query("SELECT m FROM MatchCandidate m WHERE m.lostReport.owner.id = :userId ORDER BY m.overallScore DESC")
    List<MatchCandidate> findByLostReportOwnerId(@Param("userId") Long userId);

    @Query("SELECT m FROM MatchCandidate m WHERE m.foundReport.finder.id = :userId ORDER BY m.overallScore DESC")
    List<MatchCandidate> findByFoundReportFinderId(@Param("userId") Long userId);

    Optional<MatchCandidate> findByLostReportIdAndFoundReportId(Long lostReportId, Long foundReportId);

    boolean existsByLostReportIdAndFoundReportId(Long lostReportId, Long foundReportId);

    @Query("SELECT m FROM MatchCandidate m WHERE m.overallScore >= :minScore ORDER BY m.overallScore DESC")
    List<MatchCandidate> findHighConfidenceMatches(@Param("minScore") Integer minScore);
}
