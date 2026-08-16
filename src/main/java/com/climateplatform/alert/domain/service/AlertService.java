package com.climateplatform.alert.domain.service;

import com.climateplatform.alert.domain.model.AlertRule;
import com.climateplatform.alert.domain.model.AlertSubscription;
import com.climateplatform.alert.domain.model.ClimateAlert;
import com.climateplatform.alert.domain.repository.AlertRuleRepository;
import com.climateplatform.alert.domain.repository.AlertSubscriptionRepository;
import com.climateplatform.alert.domain.repository.ClimateAlertRepository;
import com.climateplatform.alert.dto.request.CreateAlertRuleRequest;
import com.climateplatform.alert.dto.request.CreateAlertSubscriptionRequest;
import com.climateplatform.alert.dto.response.AlertRuleResponse;
import com.climateplatform.alert.dto.response.AlertSubscriptionResponse;
import com.climateplatform.alert.dto.response.ClimateAlertResponse;
import com.climateplatform.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AlertService {
    private final AlertRuleRepository alertRuleRepository;
    private final ClimateAlertRepository climateAlertRepository;
    private final AlertSubscriptionRepository alertSubscriptionRepository;

    @Cacheable("alert-rules")
    public List<AlertRuleResponse> getAllAlertRules() {
        return alertRuleRepository.findByIsActiveTrue().stream()
                .map(AlertRuleResponse::from)
                .collect(Collectors.toList());
    }

    @Cacheable("alert-rules")
    public AlertRuleResponse getAlertRuleById(UUID id) {
        AlertRule rule = alertRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertRule", "id", id));
        return AlertRuleResponse.from(rule);
    }

    @Transactional
    @CacheEvict(value = "alert-rules", allEntries = true)
    public AlertRuleResponse createAlertRule(CreateAlertRuleRequest request) {
        AlertRule rule = AlertRule.builder()
                .ruleName(request.getRuleName())
                .alertType(request.getAlertType())
                .conditionField(request.getConditionField())
                .operator(request.getOperator())
                .thresholdValue(request.getThresholdValue())
                .durationMinutes(request.getDurationMinutes())
                .severity(request.getSeverity())
                .isActive(request.getIsActive())
                .cooldownMinutes(request.getCooldownMinutes())
                .build();

        AlertRule savedRule = alertRuleRepository.save(rule);
        log.info("Created alert rule: {}", savedRule.getRuleName());
        return AlertRuleResponse.from(savedRule);
    }

    @Transactional
    @CacheEvict(value = "alert-rules", allEntries = true)
    public AlertRuleResponse updateAlertRule(UUID id, CreateAlertRuleRequest request) {
        AlertRule rule = alertRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertRule", "id", id));

        rule.setRuleName(request.getRuleName());
        rule.setAlertType(request.getAlertType());
        rule.setConditionField(request.getConditionField());
        rule.setOperator(request.getOperator());
        rule.setThresholdValue(request.getThresholdValue());
        rule.setDurationMinutes(request.getDurationMinutes());
        rule.setSeverity(request.getSeverity());
        rule.setIsActive(request.getIsActive());
        rule.setCooldownMinutes(request.getCooldownMinutes());

        AlertRule updatedRule = alertRuleRepository.save(rule);
        return AlertRuleResponse.from(updatedRule);
    }

    @Transactional
    @CacheEvict(value = "alert-rules", allEntries = true)
    public void deleteAlertRule(UUID id) {
        AlertRule rule = alertRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertRule", "id", id));
        rule.setIsActive(false);
        alertRuleRepository.save(rule);
    }

    @Cacheable("active-alerts")
    public List<ClimateAlertResponse> getActiveAlerts() {
        List<ClimateAlert> alerts = climateAlertRepository.findActiveAlerts(LocalDateTime.now());
        return alerts.stream()
                .map(ClimateAlertResponse::from)
                .collect(Collectors.toList());
    }

    @Cacheable("alerts-by-governorate")
    public List<ClimateAlertResponse> getAlertsByGovernorate(UUID governorateId) {
        List<ClimateAlert> alerts = climateAlertRepository.findByGovernorateIdAndIsResolvedFalseOrderByTriggeredAtDesc(governorateId);
        return alerts.stream()
                .map(ClimateAlertResponse::from)
                .collect(Collectors.toList());
    }

    public Page<ClimateAlertResponse> getAlertsByGovernoratePaginated(UUID governorateId, Pageable pageable) {
        Page<ClimateAlert> alerts = climateAlertRepository.findByGovernorateIdOrderByTriggeredAtDesc(governorateId, pageable);
        return alerts.map(ClimateAlertResponse::from);
    }

    @Transactional
    public ClimateAlertResponse acknowledgeAlert(UUID alertId) {
        ClimateAlert alert = climateAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("ClimateAlert", "id", alertId));

        alert.setIsAcknowledged(true);
        ClimateAlert updatedAlert = climateAlertRepository.save(alert);
        log.info("Alert acknowledged: {}", alertId);
        return ClimateAlertResponse.from(updatedAlert);
    }

    @Transactional
    public ClimateAlertResponse resolveAlert(UUID alertId) {
        ClimateAlert alert = climateAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("ClimateAlert", "id", alertId));

        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        ClimateAlert updatedAlert = climateAlertRepository.save(alert);
        log.info("Alert resolved: {}", alertId);
        return ClimateAlertResponse.from(updatedAlert);
    }

    @Cacheable("user-alert-subscriptions")
    public List<AlertSubscriptionResponse> getUserSubscriptions(UUID userId) {
        List<AlertSubscription> subscriptions = alertSubscriptionRepository.findByUserIdAndIsActiveTrue(userId);
        return subscriptions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "user-alert-subscriptions", allEntries = true)
    public AlertSubscriptionResponse createSubscription(CreateAlertSubscriptionRequest request) {
        AlertSubscription subscription = AlertSubscription.builder()
                .userId(request.getUserId())
                .governorateId(request.getGovernorateId())
                .alertType(request.getAlertType())
                .minSeverity(request.getMinSeverity())
                .isActive(request.getIsActive())
                .notificationMethod(request.getNotificationMethod())
                .build();

        AlertSubscription savedSubscription = alertSubscriptionRepository.save(subscription);
        log.info("Created subscription for user: {}", request.getUserId());
        return toResponse(savedSubscription);
    }

    @Cacheable("alert-statistics")
    public Map<String, Object> getAlertStatistics() {
        long totalAlerts = climateAlertRepository.count();
        long activeAlerts = climateAlertRepository.countActiveAlerts(LocalDateTime.now());
        long resolvedAlerts = totalAlerts - activeAlerts;

        Map<AlertRule.AlertType, Long> alertsByType = climateAlertRepository.findActiveAlerts(LocalDateTime.now()).stream()
                .collect(Collectors.groupingBy(
                        ClimateAlert::getAlertType,
                        Collectors.counting()
                ));

        Map<AlertRule.Severity, Long> alertsBySeverity = climateAlertRepository.findActiveAlerts(LocalDateTime.now()).stream()
                .collect(Collectors.groupingBy(
                        ClimateAlert::getSeverity,
                        Collectors.counting()
                ));

        return Map.of(
                "totalAlerts", totalAlerts,
                "activeAlerts", activeAlerts,
                "resolvedAlerts", resolvedAlerts,
                "alertsByType", alertsByType,
                "alertsBySeverity", alertsBySeverity
        );
    }

    @Transactional
    public void processClimateDataAndGenerateAlerts(UUID governorateId, String governorateName,
                                                     Map<String, Double> currentValues) {
        log.debug("Processing climate data for governorate: {} with values: {}", governorateName, currentValues);

        List<AlertRule> activeRules = alertRuleRepository.findByIsActiveTrue();

        for (AlertRule rule : activeRules) {
            Double currentValue = currentValues.get(rule.getConditionField());
            if (currentValue == null) {
                continue;
            }

            if (evaluateCondition(currentValue, rule.getOperator(), rule.getThresholdValue())) {
                boolean hasRecentAlert = climateAlertRepository
                        .findByGovernorateAndAlertType(governorateId, rule.getAlertType())
                        .stream()
                        .anyMatch(alert -> alert.getTriggeredAt().isAfter(
                                LocalDateTime.now().minusMinutes(rule.getCooldownMinutes())));

                if (!hasRecentAlert) {
                    ClimateAlert alert = createAlert(governorateId, governorateName, rule, currentValue);
                    climateAlertRepository.save(alert);
                    log.info("Generated alert: {} for governorate: {}", rule.getAlertType(), governorateName);
                }
            }
        }
    }

    boolean evaluateCondition(Double currentValue, String operator, Double thresholdValue) {
        return switch (operator.toUpperCase()) {
            case ">" -> currentValue > thresholdValue;
            case ">=" -> currentValue >= thresholdValue;
            case "<" -> currentValue < thresholdValue;
            case "<=" -> currentValue <= thresholdValue;
            case "=" -> currentValue.equals(thresholdValue);
            default -> false;
        };
    }

    private ClimateAlert createAlert(UUID governorateId, String governorateName, AlertRule rule, Double currentValue) {
        return ClimateAlert.builder()
                .governorateId(governorateId)
                .governorateName(governorateName)
                .alertType(rule.getAlertType())
                .severity(rule.getSeverity())
                .title(generateAlertTitle(rule.getAlertType(), currentValue))
                .description(generateAlertDescription(rule, currentValue))
                .currentValue(currentValue)
                .thresholdValue(rule.getThresholdValue())
                .triggeredAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isAcknowledged(false)
                .isResolved(false)
                .healthRecommendations(generateHealthRecommendations(rule.getAlertType()))
                .build();
    }

    private String generateAlertTitle(AlertRule.AlertType alertType, Double value) {
        return switch (alertType) {
            case HEATWAVE -> String.format("Heatwave Alert - Temperature: %.1f°C", value);
            case DROUGHT -> String.format("Drought Alert - Dryness Level: %.1f", value);
            case FIRE -> String.format("Fire Risk Alert - Risk Level: %.1f", value);
            case STORM -> String.format("Storm Alert - Wind Speed: %.1f m/s", value);
            case FLOOD -> String.format("Flood Alert - Precipitation: %.1f mm", value);
            case AIR_QUALITY -> String.format("Air Quality Alert - AQI: %.1f", value);
        };
    }

    private String generateAlertDescription(AlertRule rule, Double value) {
        return String.format("Alert triggered by %s condition: %s %s %.1f. Current value: %.1f",
                rule.getConditionField(), rule.getOperator(), rule.getThresholdValue(), value);
    }

    private String generateHealthRecommendations(AlertRule.AlertType alertType) {
        return switch (alertType) {
            case HEATWAVE -> "Avoid outdoor activities during peak heat hours (12h-16h). Stay hydrated and wear light clothing.";
            case DROUGHT -> "Conserve water usage. Monitor water quality in local supplies.";
            case FIRE -> "Avoid open fires and burning vegetation. Keep emergency evacuation routes clear.";
            case STORM -> "Stay indoors. Avoid travel unless necessary. Secure loose objects outdoors.";
            case FLOOD -> "Avoid low-lying areas. Do not walk or drive through floodwaters.";
            case AIR_QUALITY -> "Limit outdoor exertion. Use air purifiers indoors. Check on vulnerable individuals.";
        };
    }

    private AlertSubscriptionResponse toResponse(AlertSubscription subscription) {
        return AlertSubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .governorateId(subscription.getGovernorateId())
                .alertType(subscription.getAlertType())
                .minSeverity(subscription.getMinSeverity())
                .isActive(subscription.getIsActive())
                .notificationMethod(subscription.getNotificationMethod())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
