package com.climateplatform.alert.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alert_subscriptions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "governorate_id")
    private UUID governorateId;

    @Column(name = "alert_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AlertRule.AlertType alertType;

    @Column(name = "min_severity", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertRule.Severity minSeverity;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "notification_method", length = 50)
    private String notificationMethod = "IN_APP";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
