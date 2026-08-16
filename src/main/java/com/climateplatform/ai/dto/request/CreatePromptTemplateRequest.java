package com.climateplatform.ai.dto.request;

import com.climateplatform.ai.domain.model.PromptTemplate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromptTemplateRequest {
    @NotBlank(message = "Template name is required")
    private String templateName;

    @NotNull(message = "Prompt type is required")
    private PromptTemplate.PromptType promptType;

    @NotBlank(message = "Template content is required")
    private String templateContent;

    private String description;

    private Boolean isActive = true;
}
