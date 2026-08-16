package com.climateplatform.ai.dto.response;

import com.climateplatform.ai.domain.model.AIConversation;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIConversationResponse {
    private UUID id;
    private UUID userId;
    private String sessionId;
    private UUID governorateId;
    private LocalDateTime startedAt;
    private LocalDateTime lastInteractionAt;
    private Boolean isActive;
    private List<AIMessageResponse> messages;
    private LocalDateTime createdAt;

    public static AIConversationResponse from(AIConversation conversation) {
        return AIConversationResponse.builder()
                .id(conversation.getId())
                .userId(conversation.getUserId())
                .sessionId(conversation.getSessionId())
                .governorateId(conversation.getGovernorateId())
                .startedAt(conversation.getStartedAt())
                .lastInteractionAt(conversation.getLastInteractionAt())
                .isActive(conversation.getIsActive())
                .createdAt(conversation.getCreatedAt())
                .build();
    }
}
