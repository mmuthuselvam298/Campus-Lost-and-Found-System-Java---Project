package com.campusfind.repository;

import com.campusfind.entity.LostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LostReportRepository extends JpaRepository<LostReport, Long>, JpaSpecificationExecutor<LostReport> {

    Optional<LostReport> findByReferenceId(String referenceId);

    List<LostReport> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<LostReport> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);

    @Query("SELECT l.campusLocation.name, COUNT(l) FROM LostReport l WHERE l.campusLocation IS NOT NULL GROUP BY l.campusLocation.name")
    List<Object[]> countReportsByLocation();

    @Query("SELECT l.category, COUNT(l) FROM LostReport l GROUP BY l.category")
    List<Object[]> countReportsByCategory();
}
