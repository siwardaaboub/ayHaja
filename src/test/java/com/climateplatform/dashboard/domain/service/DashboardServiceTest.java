package com.climateplatform.dashboard.domain.service;

import com.climateplatform.dashboard.domain.model.HealthStatistic;
import com.climateplatform.dashboard.domain.repository.HealthStatisticRepository;
import com.climateplatform.dashboard.dto.response.DashboardSummaryResponse;
import com.climateplatform.dashboard.dto.response.HealthStatisticResponse;
import com.climateplatform.dashboard.dto.response.HealthTrendResponse;
import com.climateplatform.dashboard.dto.response.RiskDistributionResponse;
import com.climateplatform.domain.repository.UserRepository;
import com.climateplatform.exception.ResourceNotFoundException;
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
class DashboardServiceTest {

    @Mock
    private HealthStatisticRepository healthStatisticRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GovernorateRepository governorateRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private HealthStatistic testStatistic;
    private UUID statisticId;

    @BeforeEach
    void setUp() {
        statisticId = UUID.randomUUID();
        testStatistic = HealthStatistic.builder()
                .id(statisticId)
                .governorateId(UUID.randomUUID())
                .governorateName("Tunis")
                .statisticDate(LocalDateTime.now())
                .temperature(BigDecimal.valueOf(25.5))
                .humidity(BigDecimal.valueOf(65.0))
                .uvIndex(BigDecimal.valueOf(5.0))
                .heatwaveRisk(1)
                .droughtRisk(0)
                .fireRisk(0)
                .climateRiskIndex(BigDecimal.valueOf(1.5))
                .populationAffected(100000L)
                .healthAdvisory("Stay hydrated")
                .build();
    }

    @Test
    void getDashboardSummary_WhenDataExists_ShouldReturnSummary() {
        when(userRepository.count()).thenReturn(100L);
        when(governorateRepository.count()).thenReturn(24L);
        when(healthStatisticRepository.findLatestStatistics()).thenReturn(Arrays.asList(testStatistic));

        DashboardSummaryResponse result = dashboardService.getDashboardSummary();

        assertNotNull(result);
        assertEquals(100L, result.getTotalUsers());
        assertEquals(24L, result.getTotalGovernorates());
        assertEquals(BigDecimal.valueOf(25.5), result.getAvgTemperature());
        assertEquals(BigDecimal.valueOf(65.0), result.getAvgHumidity());
    }

    @Test
    void getAllHealthStatistics_WhenDataExists_ShouldReturnResponses() {
        when(healthStatisticRepository.findLatestStatistics()).thenReturn(Arrays.asList(testStatistic));

        List<HealthStatisticResponse> result = dashboardService.getAllHealthStatistics();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tunis", result.get(0).getGovernorateName());
    }

    @Test
    void getHealthStatisticById_WhenExists_ShouldReturnResponse() {
        when(healthStatisticRepository.findById(statisticId)).thenReturn(Optional.of(testStatistic));

        HealthStatisticResponse result = dashboardService.getHealthStatisticById(statisticId);

        assertNotNull(result);
        assertEquals(statisticId, result.getId());
        assertEquals("Tunis", result.getGovernorateName());
    }

    @Test
    void getHealthStatisticById_WhenNotFound_ShouldThrowException() {
        when(healthStatisticRepository.findById(statisticId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dashboardService.getHealthStatisticById(statisticId)
        );

        assertEquals("HealthStatistic not found with id : '" + statisticId + "'", exception.getMessage());
    }

    @Test
    void getHealthTrends_WhenValidRequest_ShouldReturnTrends() {
        UUID governorateId = testStatistic.getGovernorateId();
        when(healthStatisticRepository.findByGovernorateAndDateRange(
                eq(governorateId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(testStatistic));

        HealthTrendResponse result = dashboardService.getHealthTrends(governorateId, 30);

        assertNotNull(result);
        assertEquals("Tunis", result.getGovernorateName());
        assertEquals(1, result.getDataPoints().size());
    }

    @Test
    void getRiskDistribution_WhenDataExists_ShouldReturnDistribution() {
        when(healthStatisticRepository.findLatestStatistics()).thenReturn(Arrays.asList(testStatistic));

        List<RiskDistributionResponse> result = dashboardService.getRiskDistribution();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tunis", result.get(0).getGovernorateName());
        assertEquals("Moderate", result.get(0).getRiskLevel());
    }

    @Test
    void getActiveRiskAreas_WhenActiveRisksExist_ShouldReturnResponses() {
        when(healthStatisticRepository.findActiveRiskAreas()).thenReturn(Arrays.asList(testStatistic));

        List<RiskDistributionResponse> result = dashboardService.getActiveRiskAreas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getHealthRecommendations().size() > 0);
    }

    @Test
    void getRiskLevel_WithCriticalRisk_ShouldReturnCritical() {
        testStatistic.setClimateRiskIndex(BigDecimal.valueOf(3.5));
        when(healthStatisticRepository.findLatestStatistics()).thenReturn(Arrays.asList(testStatistic));

        List<RiskDistributionResponse> result = dashboardService.getRiskDistribution();

        assertEquals("Critical", result.get(0).getRiskLevel());
    }

    @Test
    void getRiskLevel_WithLowRisk_ShouldReturnLow() {
        testStatistic.setClimateRiskIndex(BigDecimal.valueOf(0.5));
        when(healthStatisticRepository.findLatestStatistics()).thenReturn(Arrays.asList(testStatistic));

        List<RiskDistributionResponse> result = dashboardService.getRiskDistribution();

        assertEquals("Low", result.get(0).getRiskLevel());
    }
}
