package com.climateplatform.admin.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsResponse {
    private Long totalUsers;
    private Long totalAdmins;
    private Long totalGovernorates;
    private Long totalClimateDataPoints;
    private Long totalAlerts;
    private Long activeAlerts;
    private Long totalConversations;
    private Long totalHealthProfiles;
    private BigDecimal avgTemperature;
    private BigDecimal avgHumidity;
    private Map<String, Long> alertsByType;
    private Map<String, Long> usersByRole;
    private Map<String, Long> dataByGovernorate;
    private Map<String, Long> alertsBySeverity;
}
