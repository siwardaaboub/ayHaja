package com.climateplatform.alert.dto.response;

import com.climateplatform.alert.domain.model.AlertRule;
import com.climateplatform.alert.domain.model.ClimateAlert;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClimateAlertResponse {
    private UUID id;
    private UUID governorateId;
    private String governorateName;
    private AlertRule.AlertType alertType;
    private AlertRule.Severity severity;
    private String title;
    private String description;
    private Double currentValue;
    private Double thresholdValue;
    private LocalDateTime triggeredAt;
    private LocalDateTime expiresAt;
    private Boolean isAcknowledged;
    private Boolean isResolved;
    private LocalDateTime resolvedAt;
    private String healthRecommendations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClimateAlertResponse from(ClimateAlert alert) {
        return ClimateAlertResponse.builder()
                .id(alert.getId())
                .governorateId(alert.getGovernorateId())
                .governorateName(alert.getGovernorateName())
                .alertType(alert.getAlertType())
                .severity(alert.getSeverity())
                .title(alert.getTitle())
                .description(alert.getDescription())
                .currentValue(alert.getCurrentValue())
                .thresholdValue(alert.getThresholdValue())
                .triggeredAt(alert.getTriggeredAt())
                .expiresAt(alert.getExpiresAt())
                .isAcknowledged(alert.getIsAcknowledged())
                .isResolved(alert.getIsResolved())
                .resolvedAt(alert.getResolvedAt())
                .healthRecommendations(alert.getHealthRecommendations())
                .createdAt(alert.getCreatedAt())
                .updatedAt(alert.getUpdatedAt())
                .build();
    }
}
