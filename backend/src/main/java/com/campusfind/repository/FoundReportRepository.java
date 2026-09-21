package com.campusfind.repository;

import com.campusfind.entity.FoundReport;
import com.campusfind.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoundReportRepository extends JpaRepository<FoundReport, Long>, JpaSpecificationExecutor<FoundReport> {

    Optional<FoundReport> findByReferenceId(String referenceId);

    List<FoundReport> findByFinderIdOrderByCreatedAtDesc(Long finderId);

    List<FoundReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    @Query("SELECT f FROM FoundReport f WHERE f.status != 'RETURNED' AND f.status != 'CANCELLED' ORDER BY f.createdAt DESC")
    List<FoundReport> findActiveReports();

    long countByStatus(ReportStatus status);

    @Query("SELECT f.campusLocation.name, COUNT(f) FROM FoundReport f WHERE f.campusLocation IS NOT NULL GROUP BY f.campusLocation.name")
    List<Object[]> countReportsByLocation();

    @Query("SELECT f.category, COUNT(f) FROM FoundReport f GROUP BY f.category")
    List<Object[]> countReportsByCategory();
}
