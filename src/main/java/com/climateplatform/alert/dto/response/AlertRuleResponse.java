package com.climateplatform.alert.dto.response;

import com.climateplatform.alert.domain.model.AlertRule;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRuleResponse {
    private UUID id;
    private String ruleName;
    private AlertRule.AlertType alertType;
    private String conditionField;
    private String operator;
    private Double thresholdValue;
    private Integer durationMinutes;
    private AlertRule.Severity severity;
    private Boolean isActive;
    private Integer cooldownMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AlertRuleResponse from(AlertRule rule) {
        return AlertRuleResponse.builder()
                .id(rule.getId())
                .ruleName(rule.getRuleName())
                .alertType(rule.getAlertType())
                .conditionField(rule.getConditionField())
                .operator(rule.getOperator())
                .thresholdValue(rule.getThresholdValue())
                .durationMinutes(rule.getDurationMinutes())
                .severity(rule.getSeverity())
                .isActive(rule.getIsActive())
                .cooldownMinutes(rule.getCooldownMinutes())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}
