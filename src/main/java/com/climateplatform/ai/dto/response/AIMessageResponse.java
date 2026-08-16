package com.climateplatform.ai.dto.response;

import com.climateplatform.ai.domain.model.AIMessage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIMessageResponse {
    private UUID id;
    private UUID conversationId;
    private AIMessage.SenderType senderType;
    private String content;
    private Integer messageIndex;
    private Integer tokensUsed;
    private Long processingTimeMs;
    private LocalDateTime createdAt;

    public static AIMessageResponse from(AIMessage message) {
        return AIMessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderType(message.getSenderType())
                .content(message.getContent())
                .messageIndex(message.getMessageIndex())
                .tokensUsed(message.getTokensUsed())
                .processingTimeMs(message.getProcessingTimeMs())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
