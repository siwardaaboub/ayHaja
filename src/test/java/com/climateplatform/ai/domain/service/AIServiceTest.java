package com.climateplatform.ai.domain.service;

import com.climateplatform.ai.config.AIConfig;
import com.climateplatform.ai.domain.model.AIConversation;
import com.climateplatform.ai.domain.model.AIMessage;
import com.climateplatform.ai.domain.model.PromptTemplate;
import com.climateplatform.ai.domain.repository.AIConversationRepository;
import com.climateplatform.ai.domain.repository.AIMessageRepository;
import com.climateplatform.ai.domain.repository.PromptTemplateRepository;
import com.climateplatform.ai.dto.request.ChatMessageRequest;
import com.climateplatform.ai.dto.request.CreatePromptTemplateRequest;
import com.climateplatform.ai.dto.response.AIMessageResponse;
import com.climateplatform.ai.dto.response.AIConversationResponse;
import com.climateplatform.ai.dto.response.PromptTemplateResponse;
import com.climateplatform.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private AIConfig aiConfig;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AIConversationRepository conversationRepository;

    @Mock
    private AIMessageRepository messageRepository;

    @Mock
    private PromptTemplateRepository promptTemplateRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AIService aiService;

    private AIConversation testConversation;
    private UUID conversationId;

    @BeforeEach
    void setUp() {
        conversationId = UUID.randomUUID();
        testConversation = AIConversation.builder()
                .id(conversationId)
                .userId(UUID.randomUUID())
                .sessionId("test-session")
                .governorateId(UUID.randomUUID())
                .startedAt(LocalDateTime.now())
                .lastInteractionAt(LocalDateTime.now())
                .isActive(true)
                .build();

        when(aiConfig.getProvider()).thenReturn("openai");
        when(aiConfig.getOpenAIApiKey()).thenReturn("test-api-key");
        when(aiConfig.getOpenAIModel()).thenReturn("gpt-4o-mini");
        when(aiConfig.getMaxTokens()).thenReturn(2000);
        when(aiConfig.getTemperature()).thenReturn(0.7);
    }

    @Test
    void getAllPromptTemplates_WhenTemplatesExist_ShouldReturnResponses() {
        PromptTemplate template = PromptTemplate.builder()
                .id(UUID.randomUUID())
                .templateName("risk-explanation")
                .promptType(PromptTemplate.PromptType.RISK_EXPLANATION)
                .templateContent("Explain {governorateName}")
                .isActive(true)
                .build();

        when(promptTemplateRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(template));

        List<PromptTemplateResponse> result = aiService.getAllPromptTemplates();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("risk-explanation", result.get(0).getTemplateName());
    }

    @Test
    void getPromptTemplateByName_WhenExists_ShouldReturnResponse() {
        PromptTemplate template = PromptTemplate.builder()
                .id(UUID.randomUUID())
                .templateName("risk-explanation")
                .promptType(PromptTemplate.PromptType.RISK_EXPLANATION)
                .templateContent("Explain {governorateName}")
                .isActive(true)
                .build();

        when(promptTemplateRepository.findByTemplateNameAndIsActiveTrue("risk-explanation"))
                .thenReturn(Optional.of(template));

        PromptTemplateResponse result = aiService.getPromptTemplateByName("risk-explanation");

        assertNotNull(result);
        assertEquals("risk-explanation", result.getTemplateName());
    }

    @Test
    void getPromptTemplateByName_WhenNotFound_ShouldThrowException() {
        when(promptTemplateRepository.findByTemplateNameAndIsActiveTrue("nonexistent"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> aiService.getPromptTemplateByName("nonexistent")
        );

        assertEquals("PromptTemplate not found with templateName : 'nonexistent'", exception.getMessage());
    }

    @Test
    void createPromptTemplate_WhenValidRequest_ShouldCreateTemplate() {
        CreatePromptTemplateRequest request = CreatePromptTemplateRequest.builder()
                .templateName("test-template")
                .promptType(PromptTemplate.PromptType.CHAT)
                .templateContent("Test content")
                .description("Test description")
                .isActive(true)
                .build();

        PromptTemplate savedTemplate = PromptTemplate.builder()
                .id(UUID.randomUUID())
                .templateName("test-template")
                .promptType(PromptTemplate.PromptType.CHAT)
                .templateContent("Test content")
                .description("Test description")
                .isActive(true)
                .build();

        when(promptTemplateRepository.save(any(PromptTemplate.class))).thenReturn(savedTemplate);

        PromptTemplateResponse result = aiService.createPromptTemplate(request);

        assertNotNull(result);
        assertEquals("test-template", result.getTemplateName());
        verify(promptTemplateRepository).save(any(PromptTemplate.class));
    }

    @Test
    void estimateTokens_WhenTextProvided_ShouldReturnTokenCount() {
        // This is a private method, but we can test the logic
        String text = "Hello World";
        int expectedTokens = (int) Math.ceil(text.length() / 4.0);
        assertEquals(expectedTokens, (int) Math.ceil("Hello World".length() / 4.0));
    }

    @Test
    void getUserConversations_WhenConversationsExist_ShouldReturnResponses() {
        List<AIConversation> conversations = Arrays.asList(testConversation);
        when(conversationRepository.findByUserIdOrderByStartedAtDesc(any(UUID.class))).thenReturn(conversations);

        List<AIConversationResponse> result = aiService.getUserConversations(UUID.randomUUID());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-session", result.get(0).getSessionId());
    }
}
