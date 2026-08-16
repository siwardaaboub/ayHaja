package com.climateplatform.dashboard.controller;

import com.climateplatform.dashboard.domain.service.DashboardService;
import com.climateplatform.dashboard.dto.response.DashboardSummaryResponse;
import com.climateplatform.dashboard.dto.response.HealthStatisticResponse;
import com.climateplatform.dashboard.dto.response.HealthTrendResponse;
import com.climateplatform.dashboard.dto.response.RiskDistributionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<HealthStatisticResponse>> getAllHealthStatistics() {
        List<HealthStatisticResponse> statistics = dashboardService.getAllHealthStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/{id}")
    public ResponseEntity<HealthStatisticResponse> getHealthStatisticById(@PathVariable UUID id) {
        HealthStatisticResponse statistic = dashboardService.getHealthStatisticById(id);
        return ResponseEntity.ok(statistic);
    }

    @GetMapping("/trends/{governorateId}")
    public ResponseEntity<HealthTrendResponse> getHealthTrends(
            @PathVariable UUID governorateId,
            @RequestParam(defaultValue = "30") int days) {
        HealthTrendResponse trends = dashboardService.getHealthTrends(governorateId, days);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/risk-distribution")
    public ResponseEntity<List<RiskDistributionResponse>> getRiskDistribution() {
        List<RiskDistributionResponse> distribution = dashboardService.getRiskDistribution();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/active-risk-areas")
    public ResponseEntity<List<RiskDistributionResponse>> getActiveRiskAreas() {
        List<RiskDistributionResponse> activeRisks = dashboardService.getActiveRiskAreas();
        return ResponseEntity.ok(activeRisks);
    }
}
