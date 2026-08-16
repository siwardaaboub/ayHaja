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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRuleRepository alertRuleRepository;

    @Mock
    private ClimateAlertRepository climateAlertRepository;

    @Mock
    private AlertSubscriptionRepository alertSubscriptionRepository;

    @InjectMocks
    private AlertService alertService;

    private AlertRule testRule;
    private UUID ruleId;

    @BeforeEach
    void setUp() {
        ruleId = UUID.randomUUID();
        testRule = AlertRule.builder()
                .id(ruleId)
                .ruleName("Heatwave Alert Rule")
                .alertType(AlertRule.AlertType.HEATWAVE)
                .conditionField("temperature")
                .operator(">")
                .thresholdValue(38.0)
                .durationMinutes(60)
                .severity(AlertRule.Severity.HIGH)
                .isActive(true)
                .cooldownMinutes(60)
                .build();
    }

    @Test
    void getAllAlertRules_WhenRulesExist_ShouldReturnResponses() {
        when(alertRuleRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testRule));

        List<AlertRuleResponse> result = alertService.getAllAlertRules();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Heatwave Alert Rule", result.get(0).getRuleName());
    }

    @Test
    void getAlertRuleById_WhenExists_ShouldReturnResponse() {
        when(alertRuleRepository.findById(ruleId)).thenReturn(Optional.of(testRule));

        AlertRuleResponse result = alertService.getAlertRuleById(ruleId);

        assertNotNull(result);
        assertEquals(ruleId, result.getId());
        assertEquals("Heatwave Alert Rule", result.getRuleName());
    }

    @Test
    void getAlertRuleById_WhenNotFound_ShouldThrowException() {
        when(alertRuleRepository.findById(ruleId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> alertService.getAlertRuleById(ruleId)
        );

        assertEquals("AlertRule not found with id : '" + ruleId + "'", exception.getMessage());
    }

    @Test
    void createAlertRule_WhenValidRequest_ShouldCreateRule() {
        CreateAlertRuleRequest request = CreateAlertRuleRequest.builder()
                .ruleName("Drought Alert Rule")
                .alertType(AlertRule.AlertType.DROUGHT)
                .conditionField("humidity")
                .operator("<")
                .thresholdValue(20.0)
                .severity(AlertRule.Severity.MEDIUM)
                .isActive(true)
                .cooldownMinutes(120)
                .build();

        AlertRule savedRule = AlertRule.builder()
                .id(UUID.randomUUID())
                .ruleName("Drought Alert Rule")
                .alertType(AlertRule.AlertType.DROUGHT)
                .conditionField("humidity")
                .operator("<")
                .thresholdValue(20.0)
                .severity(AlertRule.Severity.MEDIUM)
                .isActive(true)
                .cooldownMinutes(120)
                .build();

        when(alertRuleRepository.save(any(AlertRule.class))).thenReturn(savedRule);

        AlertRuleResponse result = alertService.createAlertRule(request);

        assertNotNull(result);
        assertEquals("Drought Alert Rule", result.getRuleName());
        verify(alertRuleRepository).save(any(AlertRule.class));
    }

    @Test
    void getActiveAlerts_WhenAlertsExist_ShouldReturnResponses() {
        ClimateAlert alert = ClimateAlert.builder()
                .id(UUID.randomUUID())
                .governorateId(UUID.randomUUID())
                .governorateName("Tunis")
                .alertType(AlertRule.AlertType.HEATWAVE)
                .severity(AlertRule.Severity.HIGH)
                .title("Heatwave Alert")
                .triggeredAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isAcknowledged(false)
                .isResolved(false)
                .build();

        when(climateAlertRepository.findActiveAlerts(any(LocalDateTime.class))).thenReturn(Arrays.asList(alert));

        List<ClimateAlertResponse> result = alertService.getActiveAlerts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Heatwave Alert", result.get(0).getTitle());
    }

    @Test
    void acknowledgeAlert_WhenValidId_ShouldAcknowledge() {
        UUID alertId = UUID.randomUUID();
        ClimateAlert alert = ClimateAlert.builder()
                .id(alertId)
                .governorateName("Tunis")
                .alertType(AlertRule.AlertType.HEATWAVE)
                .severity(AlertRule.Severity.HIGH)
                .title("Heatwave Alert")
                .isAcknowledged(false)
                .isResolved(false)
                .build();

        when(climateAlertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(climateAlertRepository.save(any(ClimateAlert.class))).thenReturn(alert);

        ClimateAlertResponse result = alertService.acknowledgeAlert(alertId);

        assertNotNull(result);
        assertTrue(result.getIsAcknowledged());
        verify(climateAlertRepository).save(any(ClimateAlert.class));
    }

    @Test
    void resolveAlert_WhenValidId_ShouldResolve() {
        UUID alertId = UUID.randomUUID();
        ClimateAlert alert = ClimateAlert.builder()
                .id(alertId)
                .governorateName("Tunis")
                .alertType(AlertRule.AlertType.HEATWAVE)
                .severity(AlertRule.Severity.HIGH)
                .title("Heatwave Alert")
                .isAcknowledged(false)
                .isResolved(false)
                .build();

        when(climateAlertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(climateAlertRepository.save(any(ClimateAlert.class))).thenReturn(alert);

        ClimateAlertResponse result = alertService.resolveAlert(alertId);

        assertNotNull(result);
        assertTrue(result.getIsResolved());
        assertNotNull(result.getResolvedAt());
    }

    @Test
    void getUserSubscriptions_WhenSubscriptionsExist_ShouldReturnResponses() {
        UUID userId = UUID.randomUUID();
        AlertSubscription subscription = AlertSubscription.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .alertType(AlertRule.AlertType.HEATWAVE)
                .minSeverity(AlertRule.Severity.MEDIUM)
                .isActive(true)
                .build();

        when(alertSubscriptionRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Arrays.asList(subscription));

        List<AlertSubscriptionResponse> result = alertService.getUserSubscriptions(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
    }

    @Test
    void getAlertStatistics_WhenDataExists_ShouldReturnStatistics() {
        when(climateAlertRepository.count()).thenReturn(100L);
        when(climateAlertRepository.countActiveAlerts(any(LocalDateTime.class))).thenReturn(10L);
        when(climateAlertRepository.findActiveAlerts(any(LocalDateTime.class))).thenReturn(Arrays.asList());

        Map<String, Object> result = alertService.getAlertStatistics();

        assertNotNull(result);
        assertEquals(100L, result.get("totalAlerts"));
        assertEquals(10L, result.get("activeAlerts"));
        assertEquals(90L, result.get("resolvedAlerts"));
    }

    @Test
    void processClimateDataAndGenerateAlerts_WhenConditionMet_ShouldGenerateAlert() {
        UUID governorateId = UUID.randomUUID();
        String governorateName = "Tunis";
        Map<String, Double> currentValues = Map.of("temperature", 42.0);

        when(alertRuleRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testRule));
        when(climateAlertRepository.findByGovernorateAndAlertType(any(UUID.class), any(AlertRule.AlertType.class)))
                .thenReturn(Arrays.asList());
        when(climateAlertRepository.save(any(ClimateAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertService.processClimateDataAndGenerateAlerts(governorateId, governorateName, currentValues);

        verify(climateAlertRepository).save(any(ClimateAlert.class));
    }

    @Test
    void processClimateDataAndGenerateAlerts_WhenConditionNotMet_ShouldNotGenerateAlert() {
        UUID governorateId = UUID.randomUUID();
        String governorateName = "Tunis";
        Map<String, Double> currentValues = Map.of("temperature", 25.0);

        when(alertRuleRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testRule));

        alertService.processClimateDataAndGenerateAlerts(governorateId, governorateName, currentValues);

        verify(climateAlertRepository, never()).save(any(ClimateAlert.class));
    }

    @Test
    void evaluateCondition_WithGreaterThanOperator_ShouldReturnCorrectResult() {
        assertTrue(alertService.evaluateCondition(42.0, ">", 38.0));
        assertFalse(alertService.evaluateCondition(35.0, ">", 38.0));
    }

    @Test
    void evaluateCondition_WithLessThanOperator_ShouldReturnCorrectResult() {
        assertTrue(alertService.evaluateCondition(15.0, "<", 20.0));
        assertFalse(alertService.evaluateCondition(25.0, "<", 20.0));
    }
}
