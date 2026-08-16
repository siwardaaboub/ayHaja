package com.climateplatform.ai.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prompt_templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "template_name", nullable = false, unique = true, length = 100)
    private String templateName;

    @Column(name = "prompt_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PromptType promptType;

    @Column(name = "template_content", columnDefinition = "TEXT", nullable = false)
    private String templateContent;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PromptType {
        RISK_EXPLANATION,
        RECOMMENDATION,
        SUMMARY,
        CHAT,
        FORECAST_ANALYSIS
    }
}
