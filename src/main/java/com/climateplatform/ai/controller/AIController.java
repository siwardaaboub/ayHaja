package com.climateplatform.ai.controller;

import com.climateplatform.ai.domain.service.AIService;
import com.climateplatform.ai.dto.request.ChatMessageRequest;
import com.climateplatform.ai.dto.request.CreatePromptTemplateRequest;
import com.climateplatform.ai.dto.response.AIMessageResponse;
import com.climateplatform.ai.dto.response.AIConversationResponse;
import com.climateplatform.ai.dto.response.PromptTemplateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    private final AIService aiService;

    @GetMapping("/conversations")
    public ResponseEntity<List<AIConversationResponse>> getUserConversations(
            @AuthenticationPrincipal UserDetails userDetails) {
        // In a real implementation, extract user ID from JWT token
        UUID userId = UUID.randomUUID(); // Placeholder
        List<AIConversationResponse> conversations = aiService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @PostMapping("/chat")
    public ResponseEntity<AIMessageResponse> processChatMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChatMessageRequest request) {
        UUID userId = UUID.randomUUID(); // Placeholder
        AIMessageResponse response = aiService.processChatMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<String> getRecommendations(
            @RequestParam UUID governorateId,
            @RequestParam String governorateName) {
        String recommendations = aiService.generateRecommendations(
                governorateId, governorateName,
                new java.util.HashMap<>(),
                new java.util.HashMap<>()
        );
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/risk-explanation")
    public ResponseEntity<String> getRiskExplanation(
            @RequestParam UUID governorateId,
            @RequestParam String governorateName) {
        String explanation = aiService.generateRiskExplanation(
                governorateId, governorateName,
                new java.util.HashMap<>(),
                new java.util.HashMap<>()
        );
        return ResponseEntity.ok(explanation);
    }

    @GetMapping("/summary")
    public ResponseEntity<String> getClimateSummary(
            @RequestParam UUID governorateId,
            @RequestParam String governorateName) {
        String summary = aiService.generateClimateSummary(
                governorateId, governorateName,
                new java.util.HashMap<>()
        );
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/templates")
    public ResponseEntity<List<PromptTemplateResponse>> getAllPromptTemplates() {
        List<PromptTemplateResponse> templates = aiService.getAllPromptTemplates();
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/templates/{name}")
    public ResponseEntity<PromptTemplateResponse> getPromptTemplateByName(@PathVariable String name) {
        PromptTemplateResponse template = aiService.getPromptTemplateByName(name);
        return ResponseEntity.ok(template);
    }

    @PostMapping("/templates")
    public ResponseEntity<PromptTemplateResponse> createPromptTemplate(
            @Valid @RequestBody CreatePromptTemplateRequest request) {
        PromptTemplateResponse template = aiService.createPromptTemplate(request);
        return ResponseEntity.ok(template);
    }
}
