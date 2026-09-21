package com.campusfind.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "campus_locations")
public class CampusLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 50)
    private String zone; // e.g., North Campus, Central Zone, Sports Area

    @Column(length = 255)
    private String description;

    private Integer floorCount;

    @Column(length = 255)
    private String commonItems; // e.g. "Water bottles, notebooks, umbrella"

    private Double mapLatitude;
    private Double mapLongitude;

    public CampusLocation() {}

    public CampusLocation(String name, String zone, String description, Integer floorCount, String commonItems, Double mapLatitude, Double mapLongitude) {
        this.name = name;
        this.zone = zone;
        this.description = description;
        this.floorCount = floorCount;
        this.commonItems = commonItems;
        this.mapLatitude = mapLatitude;
        this.mapLongitude = mapLongitude;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getFloorCount() { return floorCount; }
    public void setFloorCount(Integer floorCount) { this.floorCount = floorCount; }
    public String getCommonItems() { return commonItems; }
    public void setCommonItems(String commonItems) { this.commonItems = commonItems; }
    public Double getMapLatitude() { return mapLatitude; }
    public void setMapLatitude(Double mapLatitude) { this.mapLatitude = mapLatitude; }
    public Double getMapLongitude() { return mapLongitude; }
    public void setMapLongitude(Double mapLongitude) { this.mapLongitude = mapLongitude; }
}
