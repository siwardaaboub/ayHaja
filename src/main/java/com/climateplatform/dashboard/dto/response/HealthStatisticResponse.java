package com.climateplatform.dashboard.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatisticResponse {
    private UUID id;
    private UUID governorateId;
    private String governorateName;
    private LocalDateTime statisticDate;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal uvIndex;
    private BigDecimal airQualityIndex;
    private Integer heatwaveRisk;
    private Integer droughtRisk;
    private Integer fireRisk;
    private BigDecimal climateRiskIndex;
    private Long populationAffected;
    private String healthAdvisory;
    private LocalDateTime createdAt;
}
