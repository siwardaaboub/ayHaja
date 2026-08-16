package com.climateplatform.dashboard.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "health_statistics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "governorate_id")
    private UUID governorateId;

    @Column(name = "governorate_name", length = 100)
    private String governorateName;

    @Column(name = "statistic_date")
    private LocalDateTime statisticDate;

    @Column(name = "temperature")
    private BigDecimal temperature;

    @Column(name = "humidity")
    private BigDecimal humidity;

    @Column(name = "uv_index")
    private BigDecimal uvIndex;

    @Column(name = "air_quality_index")
    private BigDecimal airQualityIndex;

    @Column(name = "heatwave_risk")
    private Integer heatwaveRisk;

    @Column(name = "drought_risk")
    private Integer droughtRisk;

    @Column(name = "fire_risk")
    private Integer fireRisk;

    @Column(name = "climate_risk_index")
    private BigDecimal climateRiskIndex;

    @Column(name = "population_affected")
    private Long populationAffected;

    @Column(name = "health_advisory", columnDefinition = "TEXT")
    private String healthAdvisory;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
