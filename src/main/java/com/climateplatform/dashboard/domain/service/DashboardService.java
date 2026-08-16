package com.climateplatform.dashboard.domain.service;

import com.climateplatform.dashboard.domain.model.HealthStatistic;
import com.climateplatform.dashboard.domain.repository.HealthStatisticRepository;
import com.climateplatform.dashboard.dto.response.DashboardSummaryResponse;
import com.climateplatform.dashboard.dto.response.HealthStatisticResponse;
import com.climateplatform.dashboard.dto.response.HealthTrendResponse;
import com.climateplatform.dashboard.dto.response.RiskDistributionResponse;
import com.climateplatform.domain.repository.UserRepository;
import com.climateplatform.map.domain.repository.GovernorateRepository;
import com.climateplatform.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final HealthStatisticRepository healthStatisticRepository;
    private final UserRepository userRepository;
    private final GovernorateRepository governorateRepository;

    @Cacheable("dashboard-summary")
    public DashboardSummaryResponse getDashboardSummary() {
        Long totalUsers = userRepository.count();
        Long totalGovernorates = governorateRepository.count();
        List<HealthStatistic> latestStats = healthStatisticRepository.findLatestStatistics();

        Long activeAlerts = latestStats.stream()
                .filter(stat -> stat.getHeatwaveRisk() > 0 || stat.getDroughtRisk() > 0 || stat.getFireRisk() > 0)
                .count();

        BigDecimal avgTemperature = calculateAverage(latestStats.stream()
                .map(HealthStatistic::getTemperature)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        BigDecimal avgHumidity = calculateAverage(latestStats.stream()
                .map(HealthStatistic::getHumidity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        Integer highRiskAreas = latestStats.stream()
                .filter(stat -> stat.getClimateRiskIndex() != null && stat.getClimateRiskIndex().compareTo(BigDecimal.valueOf(2.5)) > 0)
                .mapToInt(stat -> stat.getClimateRiskIndex().intValue())
                .sum();

        Map<String, Long> riskDistribution = latestStats.stream()
                .collect(Collectors.groupingBy(
                        stat -> getRiskLevel(stat.getClimateRiskIndex()),
                        Collectors.counting()
                ));

        Map<String, Long> governorateUserCount = latestStats.stream()
                .collect(Collectors.toMap(
                        HealthStatistic::getGovernorateName,
                        stat -> stat.getPopulationAffected() != null ? stat.getPopulationAffected() : 0L,
                        (existing, replacement) -> existing
                ));

        return DashboardSummaryResponse.builder()
                .totalUsers(totalUsers)
                .totalGovernorates(totalGovernorates)
                .activeAlerts(activeAlerts)
                .totalClimateDataPoints((long) latestStats.size())
                .avgTemperature(avgTemperature)
                .avgHumidity(avgHumidity)
                .highRiskAreas(highRiskAreas)
                .riskDistribution(riskDistribution)
                .governorateUserCount(governorateUserCount)
                .build();
    }

    @Cacheable("health-statistics")
    public List<HealthStatisticResponse> getAllHealthStatistics() {
        return healthStatisticRepository.findLatestStatistics().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable("health-statistics")
    public HealthStatisticResponse getHealthStatisticById(UUID id) {
        HealthStatistic statistic = healthStatisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthStatistic", "id", id));
        return toResponse(statistic);
    }

    @Cacheable("health-trends")
    public HealthTrendResponse getHealthTrends(UUID governorateId, int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        List<HealthStatistic> statistics = healthStatisticRepository
                .findByGovernorateAndDateRange(governorateId, startDate, endDate);

        String governorateName = statistics.isEmpty() ? "Unknown" : statistics.get(0).getGovernorateName();

        List<HealthTrendResponse.HealthDataPoint> dataPoints = statistics.stream()
                .sorted(Comparator.comparing(HealthStatistic::getStatisticDate))
                .map(stat -> HealthTrendResponse.HealthDataPoint.builder()
                        .date(stat.getStatisticDate().toLocalDate())
                        .temperature(stat.getTemperature())
                        .humidity(stat.getHumidity())
                        .uvIndex(stat.getUvIndex())
                        .heatwaveRisk(stat.getHeatwaveRisk())
                        .droughtRisk(stat.getDroughtRisk())
                        .fireRisk(stat.getFireRisk())
                        .build())
                .collect(Collectors.toList());

        return HealthTrendResponse.builder()
                .governorateName(governorateName)
                .dataPoints(dataPoints)
                .build();
    }

    @Cacheable("risk-distribution")
    public List<RiskDistributionResponse> getRiskDistribution() {
        List<HealthStatistic> latestStats = healthStatisticRepository.findLatestStatistics();

        return latestStats.stream()
                .map(stat -> RiskDistributionResponse.builder()
                        .governorateId(stat.getGovernorateId())
                        .governorateName(stat.getGovernorateName())
                        .heatwaveRisk(stat.getHeatwaveRisk())
                        .droughtRisk(stat.getDroughtRisk())
                        .fireRisk(stat.getFireRisk())
                        .climateRiskIndex(stat.getClimateRiskIndex())
                        .populationAffected(stat.getPopulationAffected())
                        .riskLevel(getRiskLevel(stat.getClimateRiskIndex()))
                        .healthRecommendations(getHealthRecommendations(stat))
                        .build())
                .collect(Collectors.toList());
    }

    @Cacheable("active-risk-areas")
    public List<RiskDistributionResponse> getActiveRiskAreas() {
        List<HealthStatistic> activeRisks = healthStatisticRepository.findActiveRiskAreas();

        return activeRisks.stream()
                .map(stat -> RiskDistributionResponse.builder()
                        .governorateId(stat.getGovernorateId())
                        .governorateName(stat.getGovernorateName())
                        .heatwaveRisk(stat.getHeatwaveRisk())
                        .droughtRisk(stat.getDroughtRisk())
                        .fireRisk(stat.getFireRisk())
                        .climateRiskIndex(stat.getClimateRiskIndex())
                        .populationAffected(stat.getPopulationAffected())
                        .riskLevel(getRiskLevel(stat.getClimateRiskIndex()))
                        .healthRecommendations(getHealthRecommendations(stat))
                        .build())
                .collect(Collectors.toList());
    }

    private BigDecimal calculateAverage(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return values.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }

    private String getRiskLevel(BigDecimal riskIndex) {
        if (riskIndex == null) {
            return "Unknown";
        }
        if (riskIndex.compareTo(BigDecimal.valueOf(3.0)) >= 0) {
            return "Critical";
        } else if (riskIndex.compareTo(BigDecimal.valueOf(2.0)) >= 0) {
            return "High";
        } else if (riskIndex.compareTo(BigDecimal.valueOf(1.0)) >= 0) {
            return "Moderate";
        } else {
            return "Low";
        }
    }

    private List<String> getHealthRecommendations(HealthStatistic stat) {
        List<String> recommendations = new ArrayList<>();

        if (stat.getHeatwaveRisk() != null && stat.getHeatwaveRisk() > 0) {
            recommendations.add("Avoid outdoor activities during peak heat hours (12h-16h)");
            recommendations.add("Stay hydrated and wear light clothing");
        }

        if (stat.getDroughtRisk() != null && stat.getDroughtRisk() > 0) {
            recommendations.add("Conserve water usage");
            recommendations.add("Monitor water quality in local supplies");
        }

        if (stat.getFireRisk() != null && stat.getFireRisk() > 0) {
            recommendations.add("Avoid open fires and burning vegetation");
            recommendations.add("Keep emergency evacuation routes clear");
        }

        if (stat.getUvIndex() != null && stat.getUvIndex().compareTo(BigDecimal.valueOf(8)) > 0) {
            recommendations.add("Use sunscreen with SPF 30+ and wear protective clothing");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("No immediate health risks detected");
        }

        return recommendations;
    }

    private HealthStatisticResponse toResponse(HealthStatistic stat) {
        return HealthStatisticResponse.builder()
                .id(stat.getId())
                .governorateId(stat.getGovernorateId())
                .governorateName(stat.getGovernorateName())
                .statisticDate(stat.getStatisticDate())
                .temperature(stat.getTemperature())
                .humidity(stat.getHumidity())
                .uvIndex(stat.getUvIndex())
                .airQualityIndex(stat.getAirQualityIndex())
                .heatwaveRisk(stat.getHeatwaveRisk())
                .droughtRisk(stat.getDroughtRisk())
                .fireRisk(stat.getFireRisk())
                .climateRiskIndex(stat.getClimateRiskIndex())
                .populationAffected(stat.getPopulationAffected())
                .healthAdvisory(stat.getHealthAdvisory())
                .createdAt(stat.getCreatedAt())
                .build();
    }
}
