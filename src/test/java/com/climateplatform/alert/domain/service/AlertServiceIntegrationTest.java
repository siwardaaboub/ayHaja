package com.climateplatform.alert.domain.service;

import com.climateplatform.alert.domain.model.AlertRule;
import com.climateplatform.alert.domain.model.ClimateAlert;
import com.climateplatform.alert.domain.repository.AlertRuleRepository;
import com.climateplatform.alert.domain.repository.ClimateAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceIntegrationTest {

    @Mock
    private AlertRuleRepository alertRuleRepository;

    @Mock
    private ClimateAlertRepository climateAlertRepository;

    @InjectMocks
    private AlertService alertService;

    private AlertRule testRule;

    @BeforeEach
    void setUp() {
        testRule = AlertRule.builder()
                .id(UUID.randomUUID())
                .ruleName("Heatwave Alert Rule")
                .alertType(AlertRule.AlertType.HEATWAVE)
                .conditionField("temperature")
                .operator(">")
                .thresholdValue(38.0)
                .severity(AlertRule.Severity.HIGH)
                .isActive(true)
                .cooldownMinutes(60)
                .build();
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
    void processClimateDataAndGenerateAlerts_WhenCooldownActive_ShouldNotGenerateAlert() {
        UUID governorateId = UUID.randomUUID();
        String governorateName = "Tunis";
        Map<String, Double> currentValues = Map.of("temperature", 42.0);

        ClimateAlert existingAlert = ClimateAlert.builder()
                .id(UUID.randomUUID())
                .governorateId(governorateId)
                .alertType(AlertRule.AlertType.HEATWAVE)
                .triggeredAt(LocalDateTime.now().minusMinutes(30))
                .build();

        when(alertRuleRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testRule));
        when(climateAlertRepository.findByGovernorateAndAlertType(any(UUID.class), any(AlertRule.AlertType.class)))
                .thenReturn(Arrays.asList(existingAlert));

        alertService.processClimateDataAndGenerateAlerts(governorateId, governorateName, currentValues);

        verify(climateAlertRepository, never()).save(any(ClimateAlert.class));
    }

    @Test
    void evaluateCondition_WithGreaterThanOperator_ShouldEvaluateCorrectly() {
        assertTrue(alertService.evaluateCondition(42.0, ">", 38.0));
        assertFalse(alertService.evaluateCondition(35.0, ">", 38.0));
    }

    @Test
    void evaluateCondition_WithLessThanOperator_ShouldEvaluateCorrectly() {
        assertTrue(alertService.evaluateCondition(15.0, "<", 20.0));
        assertFalse(alertService.evaluateCondition(25.0, "<", 20.0));
    }

    @Test
    void evaluateCondition_WithGreaterThanOrEqualOperator_ShouldEvaluateCorrectly() {
        assertTrue(alertService.evaluateCondition(38.0, ">=", 38.0));
        assertTrue(alertService.evaluateCondition(40.0, ">=", 38.0));
        assertFalse(alertService.evaluateCondition(35.0, ">=", 38.0));
    }

    @Test
    void evaluateCondition_WithLessThanOrEqualOperator_ShouldEvaluateCorrectly() {
        assertTrue(alertService.evaluateCondition(38.0, "<=", 38.0));
        assertTrue(alertService.evaluateCondition(35.0, "<=", 38.0));
        assertFalse(alertService.evaluateCondition(40.0, "<=", 38.0));
    }

    @Test
    void evaluateCondition_WithEqualsOperator_ShouldEvaluateCorrectly() {
        assertTrue(alertService.evaluateCondition(38.0, "=", 38.0));
        assertFalse(alertService.evaluateCondition(39.0, "=", 38.0));
    }

    @Test
    void evaluateCondition_WithUnknownOperator_ShouldReturnFalse() {
        assertFalse(alertService.evaluateCondition(38.0, "unknown", 38.0));
    }
}
