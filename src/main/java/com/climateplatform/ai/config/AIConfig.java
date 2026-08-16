package com.climateplatform.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    @Value("${ai.openai.api-key:}")
    private String openAIApiKey;

    @Value("${ai.openai.model:gpt-4o-mini}")
    private String openAIModel;

    @Value("${ai.openai.max-tokens:2000}")
    private Integer maxTokens;

    @Value("${ai.openai.temperature:0.7}")
    private Double temperature;

    @Value("${ai.openai.timeout-seconds:30}")
    private Integer timeoutSeconds;

    @Value("${ai.anthropic.api-key:}")
    private String anthropicApiKey;

    @Value("${ai.anthropic.model:claude-3-sonnet-20240229}")
    private String anthropicModel;

    @Value("${ai.provider:openai}")
    private String provider;

    public String getOpenAIApiKey() {
        return openAIApiKey;
    }

    public String getOpenAIModel() {
        return openAIModel;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public String getAnthropicApiKey() {
        return anthropicApiKey;
    }

    public String getAnthropicModel() {
        return anthropicModel;
    }

    public String getProvider() {
        return provider;
    }
}
