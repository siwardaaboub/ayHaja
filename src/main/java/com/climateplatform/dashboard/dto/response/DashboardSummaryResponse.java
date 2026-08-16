package com.climateplatform.dashboard.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private Long totalUsers;
    private Long totalGovernorates;
    private Long activeAlerts;
    private Long totalClimateDataPoints;
    private BigDecimal avgTemperature;
    private BigDecimal avgHumidity;
    private Integer highRiskAreas;
    private Map<String, Long> riskDistribution;
    private Map<String, Long> governorateUserCount;
}
