package com.climateplatform.alert.dto.request;

import com.climateplatform.alert.domain.model.AlertRule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlertRuleRequest {
    @NotBlank(message = "Rule name is required")
    @Size(max = 100, message = "Rule name must not exceed 100 characters")
    private String ruleName;

    @NotNull(message = "Alert type is required")
    private AlertRule.AlertType alertType;

    @NotBlank(message = "Condition field is required")
    private String conditionField;

    @NotBlank(message = "Operator is required")
    private String operator;

    @NotNull(message = "Threshold value is required")
    private Double thresholdValue;

    private Integer durationMinutes;

    @NotNull(message = "Severity is required")
    private AlertRule.Severity severity;

    private Boolean isActive = true;

    private Integer cooldownMinutes = 60;
}
