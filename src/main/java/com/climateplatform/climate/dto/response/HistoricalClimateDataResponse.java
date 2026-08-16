package com.climateplatform.climate.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalClimateDataResponse {
    private UUID id;
    private UUID governorateId;
    private String governorateName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDate dateRecorded;
    private BigDecimal temperatureAvg;
    private BigDecimal temperatureMax;
    private BigDecimal temperatureMin;
    private BigDecimal humidity;
    private BigDecimal precipitation;
    private BigDecimal windSpeed;
    private BigDecimal pressure;
    private BigDecimal uvIndex;
    private String dataSource;
    private LocalDateTime createdAt;
}
