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
import com.climateplatform.ai.exception.AIServiceException;
import com.climateplatform.ai.exception.AIPromptException;
import com.climateplatform.ai.prompt.PromptTemplates;
import com.climateplatform.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AIService {
    private final AIConfig aiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AIConversationRepository conversationRepository;
    private final AIMessageRepository messageRepository;
    private final PromptTemplateRepository promptTemplateRepository;

    @Cacheable("ai-conversations")
    public List<AIConversationResponse> getUserConversations(UUID userId) {
        return conversationRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(AIConversationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AIMessageResponse processChatMessage(UUID userId, ChatMessageRequest request) {
        log.info("Processing chat message for user: {}", userId);

        AIConversation conversation = getOrCreateConversation(userId, request.getSessionId(), request.getGovernorateId());

        long startTime = System.currentTimeMillis();

        AIMessage userMessage = AIMessage.builder()
                .conversationId(conversation.getId())
                .senderType(AIMessage.SenderType.USER)
                .content(request.getContent())
                .messageIndex(getNextMessageIndex(conversation.getId()))
                .build();

        messageRepository.save(userMessage);

        String response = generateAIResponse(userId, request, conversation);

        long processingTime = System.currentTimeMillis() - startTime;

        AIMessage assistantMessage = AIMessage.builder()
                .conversationId(conversation.getId())
                .senderType(AIMessage.SenderType.ASSISTANT)
                .content(response)
                .messageIndex(getNextMessageIndex(conversation.getId()))
                .processingTimeMs(processingTime)
                .tokensUsed(estimateTokens(request.getContent() + response))
                .build();

        AIMessage savedMessage = messageRepository.save(assistantMessage);

        conversation.setLastInteractionAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return AIMessageResponse.from(savedMessage);
    }

    @Cacheable("prompt-templates")
    public List<PromptTemplateResponse> getAllPromptTemplates() {
        return promptTemplateRepository.findByIsActiveTrue().stream()
                .map(PromptTemplateResponse::from)
                .collect(Collectors.toList());
    }

    @Cacheable("prompt-templates")
    public PromptTemplateResponse getPromptTemplateByName(String templateName) {
        PromptTemplate template = promptTemplateRepository.findByTemplateNameAndIsActiveTrue(templateName)
                .orElseThrow(() -> new ResourceNotFoundException("PromptTemplate", "templateName", templateName));
        return PromptTemplateResponse.from(template);
    }

    @Transactional
    @CacheEvict(value = "prompt-templates", allEntries = true)
    public PromptTemplateResponse createPromptTemplate(CreatePromptTemplateRequest request) {
        PromptTemplate template = PromptTemplate.builder()
                .templateName(request.getTemplateName())
                .promptType(request.getPromptType())
                .templateContent(request.getTemplateContent())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .build();

        PromptTemplate savedTemplate = promptTemplateRepository.save(template);
        log.info("Created prompt template: {}", savedTemplate.getTemplateName());
        return PromptTemplateResponse.from(savedTemplate);
    }

    @Transactional
    public String generateRiskExplanation(UUID governorateId, String governorateName,
                                         Map<String, Object> climateData,
                                         Map<String, Object> healthProfile) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("governorateName", governorateName);
        variables.putAll(climateData);
        variables.put("healthProfile", healthProfile != null ? healthProfile.toString() : "No health profile provided");

        String prompt = PromptTemplates.formatTemplate(PromptTemplates.RISK_EXPLANATION_TEMPLATE, variables);

        return callAIService(prompt);
    }

    @Transactional
    public String generateRecommendations(UUID governorateId, String governorateName,
                                         Map<String, Object> climateData,
                                         Map<String, Object> healthProfile) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("governorateName", governorateName);
        variables.putAll(climateData);
        variables.put("healthProfile", healthProfile != null ? healthProfile.toString() : "No health profile provided");
        variables.put("forecastSummary", "See current conditions above");

        String prompt = PromptTemplates.formatTemplate(PromptTemplates.RECOMMENDATION_TEMPLATE, variables);

        return callAIService(prompt);
    }

    @Transactional
    public String generateClimateSummary(UUID governorateId, String governorateName,
                                        Map<String, Object> climateData) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("governorateName", governorateName);
        variables.putAll(climateData);

        String prompt = PromptTemplates.formatTemplate(PromptTemplates.SUMMARY_TEMPLATE, variables);

        return callAIService(prompt);
    }

    private AIConversation getOrCreateConversation(UUID userId, String sessionId, UUID governorateId) {
        if (sessionId != null) {
            Optional<AIConversation> existing = conversationRepository.findBySessionId(sessionId);
            if (existing.isPresent() && existing.get().getIsActive()) {
                return existing.get();
            }
        }

        AIConversation conversation = AIConversation.builder()
                .userId(userId)
                .sessionId(sessionId != null ? sessionId : UUID.randomUUID().toString())
                .governorateId(governorateId)
                .startedAt(LocalDateTime.now())
                .lastInteractionAt(LocalDateTime.now())
                .isActive(true)
                .build();

        return conversationRepository.save(conversation);
    }

    private int getNextMessageIndex(UUID conversationId) {
        List<AIMessage> messages = messageRepository.findByConversationIdOrderByMessageIndexAsc(conversationId);
        return messages.isEmpty() ? 0 : messages.get(messages.size() - 1).getMessageIndex() + 1;
    }

    private String generateAIResponse(UUID userId, ChatMessageRequest request, AIConversation conversation) {
        Map<String, Object> context = new HashMap<>();
        context.put("userQuery", request.getContent());

        if (request.getGovernorateId() != null) {
            context.put("governorateId", request.getGovernorateId().toString());
        }

        if (request.getContext() != null) {
            context.put("context", request.getContext());
        }

        List<AIMessage> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
        String conversationHistory = history.stream()
                .map(msg -> msg.getSenderType() + ": " + msg.getContent())
                .collect(Collectors.joining("\n"));

        context.put("conversationHistory", conversationHistory);

        Map<String, Object> variables = new HashMap<>();
        variables.putAll(context);
        variables.put("governorateName", conversation.getGovernorateId() != null ? "Selected Location" : "Tunisia");

        String prompt = PromptTemplates.formatTemplate(PromptTemplates.CHAT_TEMPLATE, variables);

        return callAIService(prompt);
    }

    private String callAIService(String prompt) {
        if ("openai".equalsIgnoreCase(aiConfig.getProvider())) {
            return callOpenAI(prompt);
        } else if ("anthropic".equalsIgnoreCase(aiConfig.getProvider())) {
            return callAnthropic(prompt);
        } else {
            throw new AIServiceException("Unknown", "No AI provider configured");
        }
    }

    private String callOpenAI(String prompt) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://api.openai.com/v1/chat/completions")
                    .toUriString();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getOpenAIModel());
            requestBody.put("max_tokens", aiConfig.getMaxTokens());
            requestBody.put("temperature", aiConfig.getTemperature());

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getOpenAIApiKey());

            org.springframework.http.HttpEntity<Map<String, Object>> request =
                    new org.springframework.http.HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(url, request, String.class);

            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }

            throw new AIServiceException("OpenAI", "No response generated");
        } catch (RestClientException e) {
            log.error("Failed to call OpenAI API", e);
            throw new AIServiceException("OpenAI", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process OpenAI response", e);
            throw new AIServiceException("OpenAI", "Failed to process response");
        }
    }

    private String callAnthropic(String prompt) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://api.anthropic.com/v1/messages")
                    .toUriString();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getAnthropicModel());
            requestBody.put("max_tokens", aiConfig.getMaxTokens());

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", messages);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("x-api-key", aiConfig.getAnthropicApiKey());
            headers.set("anthropic-version", "2023-06-01");

            org.springframework.http.HttpEntity<Map<String, Object>> request =
                    new org.springframework.http.HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(url, request, String.class);

            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> content = (List<Map<String, Object>>) responseMap.get("content");

            if (content != null && !content.isEmpty()) {
                return (String) content.get(0).get("text");
            }

            throw new AIServiceException("Anthropic", "No response generated");
        } catch (RestClientException e) {
            log.error("Failed to call Anthropic API", e);
            throw new AIServiceException("Anthropic", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process Anthropic response", e);
            throw new AIServiceException("Anthropic", "Failed to process response");
        }
    }

    private int estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil(text.length() / 4.0);
    }
}
