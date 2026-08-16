package com.climateplatform.ai.dto.response;

import com.climateplatform.ai.domain.model.PromptTemplate;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplateResponse {
    private UUID id;
    private String templateName;
    private PromptTemplate.PromptType promptType;
    private String templateContent;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PromptTemplateResponse from(PromptTemplate template) {
        return PromptTemplateResponse.builder()
                .id(template.getId())
                .templateName(template.getTemplateName())
                .promptType(template.getPromptType())
                .templateContent(template.getTemplateContent())
                .description(template.getDescription())
                .isActive(template.getIsActive())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
