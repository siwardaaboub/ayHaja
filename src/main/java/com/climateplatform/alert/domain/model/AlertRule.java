package com.climateplatform.alert.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alert_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "alert_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AlertType alertType;

    @Column(name = "condition_field", nullable = false, length = 50)
    private String conditionField;

    @Column(name = "operator", nullable = false, length = 10)
    private String operator;

    @Column(name = "threshold_value", nullable = false)
    private Double thresholdValue;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "severity", nullable = false)
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "cooldown_minutes")
    private Integer cooldownMinutes = 60;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum AlertType {
        HEATWAVE, DROUGHT, FIRE, STORM, FLOOD, AIR_QUALITY
    }

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
