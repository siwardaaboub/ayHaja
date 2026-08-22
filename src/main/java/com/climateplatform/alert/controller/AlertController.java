package com.climateplatform.alert.controller;

import com.climateplatform.alert.domain.service.AlertService;
import com.climateplatform.risk.domain.service.RiskAnalysisService;
import com.climateplatform.alert.dto.request.CreateAlertRuleRequest;
import com.climateplatform.alert.dto.request.CreateAlertSubscriptionRequest;
import com.climateplatform.alert.dto.response.AlertRuleResponse;
import com.climateplatform.alert.dto.response.AlertSubscriptionResponse;
import com.climateplatform.alert.dto.response.ClimateAlertResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;
        private final RiskAnalysisService riskAnalysisService;

    @GetMapping("/rules")
    public ResponseEntity<List<AlertRuleResponse>> getAllAlertRules() {
        List<AlertRuleResponse> rules = alertService.getAllAlertRules();
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/rules/{id}")
    public ResponseEntity<AlertRuleResponse> getAlertRuleById(@PathVariable UUID id) {
        AlertRuleResponse rule = alertService.getAlertRuleById(id);
        return ResponseEntity.ok(rule);
    }

    @PostMapping("/rules")
    public ResponseEntity<AlertRuleResponse> createAlertRule(@Valid @RequestBody CreateAlertRuleRequest request) {
        AlertRuleResponse rule = alertService.createAlertRule(request);
        return ResponseEntity.ok(rule);
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<AlertRuleResponse> updateAlertRule(
            @PathVariable UUID id,
            @Valid @RequestBody CreateAlertRuleRequest request) {
        AlertRuleResponse rule = alertService.updateAlertRule(id, request);
        return ResponseEntity.ok(rule);
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<Void> deleteAlertRule(@PathVariable UUID id) {
        alertService.deleteAlertRule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<ClimateAlertResponse>> getActiveAlerts() {
        List<ClimateAlertResponse> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/governorate/{governorateId}")
    public ResponseEntity<List<ClimateAlertResponse>> getAlertsByGovernorate(@PathVariable UUID governorateId) {
        List<ClimateAlertResponse> alerts = alertService.getAlertsByGovernorate(governorateId);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/governorate/{governorateId}/paginated")
    public ResponseEntity<Page<ClimateAlertResponse>> getAlertsByGovernoratePaginated(
            @PathVariable UUID governorateId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ClimateAlertResponse> alerts = alertService.getAlertsByGovernoratePaginated(governorateId, pageable);
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<ClimateAlertResponse> acknowledgeAlert(@PathVariable UUID id) {
        ClimateAlertResponse alert = alertService.acknowledgeAlert(id);
        return ResponseEntity.ok(alert);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<ClimateAlertResponse> resolveAlert(@PathVariable UUID id) {
        ClimateAlertResponse alert = alertService.resolveAlert(id);
        return ResponseEntity.ok(alert);
    }

    @GetMapping("/subscriptions/user/{userId}")
    public ResponseEntity<List<AlertSubscriptionResponse>> getUserSubscriptions(@PathVariable UUID userId) {
        List<AlertSubscriptionResponse> subscriptions = alertService.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<AlertSubscriptionResponse> createSubscription(
            @Valid @RequestBody CreateAlertSubscriptionRequest request) {
        AlertSubscriptionResponse subscription = alertService.createSubscription(request);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAlertStatistics() {
        Map<String, Object> statistics = alertService.getAlertStatistics();
        return ResponseEntity.ok(statistics);
    }
        @PostMapping("/trigger-test")
    public ResponseEntity<Void> triggerAlertsTest() {
        riskAnalysisService.triggerAlertsOnlyForToday();
        return ResponseEntity.ok().build();
    }
}
