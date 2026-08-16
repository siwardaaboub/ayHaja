package com.climateplatform.dashboard.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthTrendResponse {
    private String governorateName;
    private List<HealthDataPoint> dataPoints;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthDataPoint {
        private LocalDate date;
        private BigDecimal temperature;
        private BigDecimal humidity;
        private BigDecimal uvIndex;
        private Integer heatwaveRisk;
        private Integer droughtRisk;
        private Integer fireRisk;
    }
}
