package com.climateplatform.map.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Geometry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "governorates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Governorate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "name_ar", length = 100)
    private String nameAr;

    @Column(name = "center_lat", nullable = false, precision = 10, scale = 8)
    private BigDecimal centerLat;

    @Column(name = "center_lon", nullable = false, precision = 11, scale = 8)
    private BigDecimal centerLon;

    @Column(name = "population")
    private Long population;

    @Column(name = "area_km2")
    private BigDecimal areaKm2;

    @Column(columnDefinition = "geometry(MultiPolygon, 4326)")
    private Geometry geometry;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
