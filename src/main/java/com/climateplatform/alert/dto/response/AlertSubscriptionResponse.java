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
public class AlertSubscriptionResponse {
    private UUID id;
    private UUID userId;
    private UUID governorateId;
    private AlertRule.AlertType alertType;
    private AlertRule.Severity minSeverity;
    private Boolean isActive;
    private String notificationMethod;
    private LocalDateTime createdAt;
}
