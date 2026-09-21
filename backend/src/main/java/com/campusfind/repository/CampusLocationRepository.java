package com.campusfind.repository;

import com.campusfind.entity.CampusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampusLocationRepository extends JpaRepository<CampusLocation, Long> {
    Optional<CampusLocation> findByNameIgnoreCase(String name);
}
