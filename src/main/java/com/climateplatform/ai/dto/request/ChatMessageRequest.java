package com.climateplatform.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    @NotBlank(message = "Message content is required")
    private String content;

    private UUID governorateId;

    private String sessionId;

    private String context;
}
