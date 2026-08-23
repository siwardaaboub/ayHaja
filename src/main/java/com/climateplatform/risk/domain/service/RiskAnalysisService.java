package com.climateplatform.risk.domain.service;

import com.climateplatform.alert.domain.model.AlertRule;
import com.climateplatform.alert.domain.service.AlertService;
import com.climateplatform.climate.domain.model.ClimateData;
import com.climateplatform.climate.domain.repository.ClimateDataRepository;
import com.climateplatform.dashboard.domain.model.HealthStatistic;
import com.climateplatform.dashboard.domain.repository.HealthStatisticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskAnalysisService {

    private final ClimateDataRepository climateDataRepository;
    private final HealthStatisticRepository healthStatisticRepository;
    private final AlertService alertService;

    @Transactional
    public void calculateRisksForToday() {

        LocalDate today = LocalDate.now();

        List<ClimateData> climateDataList =
                climateDataRepository.findByDate(today);

        log.info("Calculating risks for {} climate records on {}", climateDataList.size(), today);

        for (ClimateData climateData : climateDataList) {

            int heatwaveRisk = calculateHeatwaveRisk(climateData);
            int droughtRisk = calculateDroughtRisk(climateData);
            int fireRisk = calculateFireRisk(climateData);

            BigDecimal climateRiskIndex = calculateClimateRiskIndex(
                    heatwaveRisk,
                    droughtRisk,
                    fireRisk
            );

            HealthStatistic statistic = HealthStatistic.builder()
                    .governorateId(climateData.getGovernorateId())
                    .governorateName(climateData.getGovernorateName())
                    .statisticDate(LocalDateTime.now())
                    .temperature(climateData.getTemperature())
                    .humidity(climateData.getHumidity())
                    .uvIndex(climateData.getUvIndex())
                    .airQualityIndex(null)
                    .heatwaveRisk(heatwaveRisk)
                    .droughtRisk(droughtRisk)
                    .fireRisk(fireRisk)
                    .climateRiskIndex(climateRiskIndex)
                    .populationAffected(0L)
                    .healthAdvisory(buildHealthAdvisory(
                            heatwaveRisk,
                            droughtRisk,
                            fireRisk
                    ))
                    .build();

            healthStatisticRepository.save(statistic);

            Map<String, Double> values = new HashMap<>();

            if (climateData.getTemperature() != null) {
                values.put(
                        "temperature",
                        climateData.getTemperature().doubleValue()
                );
            }

            if (climateData.getHumidity() != null) {
                values.put(
                        "humidity",
                        climateData.getHumidity().doubleValue()
                );
            }

            if (climateData.getPrecipitation() != null) {
                values.put(
                        "precipitation",
                        climateData.getPrecipitation().doubleValue()
                );
            }

            if (climateData.getWindSpeed() != null) {
                values.put(
                        "windSpeed",
                        climateData.getWindSpeed().doubleValue()
                );
            }

            if (climateData.getUvIndex() != null) {
                values.put(
                        "uvIndex",
                        climateData.getUvIndex().doubleValue()
                );
            }

            alertService.processClimateDataAndGenerateAlerts(
                    climateData.getGovernorateId(),
                    climateData.getGovernorateName(),
                    values
            );
        }

        log.info("Risk analysis completed for {} governorates", climateDataList.size());
    }

    private int calculateHeatwaveRisk(ClimateData climateData) {

        if (climateData.getTemperature() == null) {
            return 0;
        }

        double temperature = climateData.getTemperature().doubleValue();

        if (temperature >= 45) {
            return 4;
        } else if (temperature >= 40) {
            return 3;
        } else if (temperature >= 35) {
            return 2;
        } else if (temperature >= 30) {
            return 1;
        }

        return 0;
    }

    private int calculateDroughtRisk(ClimateData climateData) {

        if (climateData.getHumidity() == null &&
                climateData.getPrecipitation() == null) {
            return 0;
        }

        double humidity = climateData.getHumidity() != null
                ? climateData.getHumidity().doubleValue()
                : 100.0;

        double precipitation = climateData.getPrecipitation() != null
                ? climateData.getPrecipitation().doubleValue()
                : 0.0;

        if (humidity < 20 && precipitation <= 1) {
            return 4;
        } else if (humidity < 30 && precipitation <= 2) {
            return 3;
        } else if (humidity < 40 && precipitation <= 3) {
            return 2;
        } else if (humidity < 50 && precipitation <= 5) {
            return 1;
        }

        return 0;
    }

    private int calculateFireRisk(ClimateData climateData) {

        if (climateData.getTemperature() == null &&
                climateData.getHumidity() == null) {
            return 0;
        }

        double temperature = climateData.getTemperature() != null
                ? climateData.getTemperature().doubleValue()
                : 0.0;

        double humidity = climateData.getHumidity() != null
                ? climateData.getHumidity().doubleValue()
                : 100.0;

        double windSpeed = climateData.getWindSpeed() != null
                ? climateData.getWindSpeed().doubleValue()
                : 0.0;

        if (temperature >= 40 && humidity < 20 && windSpeed >= 20) {
            return 4;
        } else if (temperature >= 35 && humidity < 30 && windSpeed >= 15) {
            return 3;
        } else if (temperature >= 35 && humidity < 40) {
            return 2;
        } else if (temperature >= 30 && humidity < 50) {
            return 1;
        }

        return 0;
    }

    private BigDecimal calculateClimateRiskIndex(
            int heatwaveRisk,
            int droughtRisk,
            int fireRisk) {

        double average = (
                heatwaveRisk +
                droughtRisk +
                fireRisk
        ) / 3.0;

        return BigDecimal.valueOf(average)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String buildHealthAdvisory(
            int heatwaveRisk,
            int droughtRisk,
            int fireRisk) {

        StringBuilder advisory = new StringBuilder();

        if (heatwaveRisk > 0) {
            advisory.append(
                    "Avoid outdoor activities during peak heat hours (12h-16h). "
            );
            advisory.append(
                    "Stay hydrated and wear light clothing. "
            );
        }

        if (droughtRisk > 0) {
            advisory.append(
                    "Conserve water usage and monitor local water supplies. "
            );
        }

        if (fireRisk > 0) {
            advisory.append(
                    "Avoid open fires and burning vegetation. "
            );
        }

        if (advisory.isEmpty()) {
            advisory.append("No immediate health risks detected.");
        }

        return advisory.toString().trim();
    }
        @Transactional
    public void triggerAlertsOnlyForToday() {
        LocalDate today = LocalDate.now();
        List<ClimateData> climateDataList = climateDataRepository.findByDate(today);

        for (ClimateData climateData : climateDataList) {
            Map<String, Double> values = new HashMap<>();

            if (climateData.getTemperature() != null) {
                values.put("temperature", climateData.getTemperature().doubleValue());
            }
            if (climateData.getHumidity() != null) {
                values.put("humidity", climateData.getHumidity().doubleValue());
            }
            if (climateData.getPrecipitation() != null) {
                values.put("precipitation", climateData.getPrecipitation().doubleValue());
            }
            if (climateData.getWindSpeed() != null) {
                values.put("windSpeed", climateData.getWindSpeed().doubleValue());
            }
            if (climateData.getUvIndex() != null) {
                values.put("uvIndex", climateData.getUvIndex().doubleValue());
            }

            alertService.processClimateDataAndGenerateAlerts(
                    climateData.getGovernorateId(),
                    climateData.getGovernorateName(),
                    values
            );
        }
    }
}