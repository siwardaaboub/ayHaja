package com.climateplatform.dashboard.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskDistributionResponse {
    private UUID governorateId;
    private String governorateName;
    private BigDecimal centerLat;
    private BigDecimal centerLon;
    private Integer heatwaveRisk;
    private Integer droughtRisk;
    private Integer fireRisk;
    private BigDecimal climateRiskIndex;
    private Long populationAffected;
    private String riskLevel;
    private List<String> healthRecommendations;
}
