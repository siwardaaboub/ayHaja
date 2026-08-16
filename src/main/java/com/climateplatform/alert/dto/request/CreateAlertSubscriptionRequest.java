package com.climateplatform.alert.dto.request;

import com.climateplatform.alert.domain.model.AlertRule;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlertSubscriptionRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    private UUID governorateId;

    @NotNull(message = "Alert type is required")
    private AlertRule.AlertType alertType;

    @NotNull(message = "Minimum severity is required")
    private AlertRule.Severity minSeverity;

    private Boolean isActive = true;

    private String notificationMethod = "IN_APP";
}
