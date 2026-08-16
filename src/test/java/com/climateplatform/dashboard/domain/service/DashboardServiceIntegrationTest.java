package com.climateplatform.dashboard.domain.service;

import com.climateplatform.dashboard.domain.model.HealthStatistic;
import com.climateplatform.dashboard.domain.repository.HealthStatisticRepository;
import com.climateplatform.dashboard.dto.response.DashboardSummaryResponse;
import com.climateplatform.dashboard.dto.response.RiskDistributionResponse;
import com.climateplatform.domain.repository.UserRepository;
import com.climateplatform.map.domain.repository.GovernorateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceIntegrationTest {

    @Mock
    private HealthStatisticRepository healthStatisticRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GovernorateRepository governorateRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getDashboardSummary_WhenDataExists_ShouldReturnCorrectSummary() {
        when(userRepository.count()).thenReturn(100L);
        when(governorateRepository.count()).thenReturn(24L);
        when(healthStatisticRepository.findLatestStatistics()).thenReturn(Arrays.asList(
                createTestStatistic("Tunis", BigDecimal.valueOf(25.5), 1, 0, 0, BigDecimal.valueOf(1.5)),
                createTestStatistic("Sousse", BigDecimal.valueOf(28.0), 2, 1, 0, BigDecimal.valueOf(2.5))
        ));

        DashboardSummaryResponse result = dashboardService.getDashboardSummary();

        assertNotNull(result);
        assertEquals(100L, result.getTotalUsers());
        assertEquals(24L, result.getTotalGovernorates());
        assertEquals(BigDecimal.valueOf(26.75), result.getAvgTemperature().setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    void getRiskDistribution_WhenDataExists_ShouldReturnDistribution() {
        List<HealthStatistic> stats = Arrays.asList(
                createTestStatistic("Tunis", BigDecimal.valueOf(25.5), 1, 0, 0, BigDecimal.valueOf(1.5)),
                createTestStatistic("Sousse", BigDecimal.valueOf(28.0), 2, 1, 0, BigDecimal.valueOf(2.5))
        );

        when(healthStatisticRepository.findLatestStatistics()).thenReturn(stats);

        List<RiskDistributionResponse> result = dashboardService.getRiskDistribution();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Moderate", result.get(0).getRiskLevel());
        assertEquals("High", result.get(1).getRiskLevel());
    }

    @Test
    void getRiskLevel_WithNullRiskIndex_ShouldReturnUnknown() {
        HealthStatistic stat = createTestStatistic("Test", BigDecimal.valueOf(25.0), 0, 0, 0, null);
        when(healthStatisticRepository.findLatestStatistics()).thenReturn(Arrays.asList(stat));

        List<RiskDistributionResponse> result = dashboardService.getRiskDistribution();

        assertEquals("Unknown", result.get(0).getRiskLevel());
    }

    @Test
    void getHealthRecommendations_ForHeatwave_ShouldIncludeHeatwaveAdvice() {
        HealthStatistic stat = HealthStatistic.builder()
                .heatwaveRisk(2)
                .uvIndex(BigDecimal.valueOf(9.0))
                .build();

        when(healthStatisticRepository.findLatestStatistics()).thenReturn(Arrays.asList(stat));

        List<RiskDistributionResponse> result = dashboardService.getRiskDistribution();

        assertTrue(result.get(0).getHealthRecommendations().stream()
                .anyMatch(rec -> rec.contains("Avoid outdoor activities")));
    }

    private HealthStatistic createTestStatistic(String governorateName, BigDecimal temperature,
                                               Integer heatwaveRisk, Integer droughtRisk,
                                               Integer fireRisk, BigDecimal climateRiskIndex) {
        return HealthStatistic.builder()
                .id(UUID.randomUUID())
                .governorateId(UUID.randomUUID())
                .governorateName(governorateName)
                .statisticDate(LocalDateTime.now())
                .temperature(temperature)
                .humidity(BigDecimal.valueOf(65.0))
                .uvIndex(BigDecimal.valueOf(5.0))
                .heatwaveRisk(heatwaveRisk)
                .droughtRisk(droughtRisk)
                .fireRisk(fireRisk)
                .climateRiskIndex(climateRiskIndex)
                .populationAffected(100000L)
                .build();
    }
}
