package com.campusfind.controller;

import com.campusfind.entity.CampusLocation;
import com.campusfind.entity.ItemCategory;
import com.campusfind.repository.CampusLocationRepository;
import com.campusfind.repository.ItemCategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LocationCategoryController {

    private final CampusLocationRepository campusLocationRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    public LocationCategoryController(
            CampusLocationRepository campusLocationRepository,
            ItemCategoryRepository itemCategoryRepository) {
        this.campusLocationRepository = campusLocationRepository;
        this.itemCategoryRepository = itemCategoryRepository;
    }

    @GetMapping("/locations")
    public ResponseEntity<List<CampusLocation>> getAllLocations() {
        return ResponseEntity.ok(campusLocationRepository.findAll());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ItemCategory>> getAllCategories() {
        return ResponseEntity.ok(itemCategoryRepository.findAll());
    }
}
