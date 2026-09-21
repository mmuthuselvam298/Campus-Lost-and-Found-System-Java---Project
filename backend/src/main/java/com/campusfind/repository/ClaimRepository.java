package com.campusfind.repository;

import com.campusfind.entity.Claim;
import com.campusfind.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByClaimantIdOrderByCreatedAtDesc(Long claimantId);

    List<Claim> findByFoundReportIdOrderByCreatedAtDesc(Long foundReportId);

    List<Claim> findByStatusOrderByCreatedAtDesc(ClaimStatus status);

    boolean existsByFoundReportIdAndClaimantIdAndStatusNot(Long foundReportId, Long claimantId, ClaimStatus status);

    long countByStatus(ClaimStatus status);

    @Query("SELECT c FROM Claim c WHERE c.foundReport.finder.id = :finderId ORDER BY c.createdAt DESC")
    List<Claim> findClaimsForFinder(@Param("finderId") Long finderId);
}
