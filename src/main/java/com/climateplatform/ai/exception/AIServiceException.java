package com.climateplatform.ai.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class AIServiceException extends RuntimeException {
    private final String service;
    private final String message;

    public AIServiceException(String service, String message) {
        super(String.format("AI service error - %s: %s", service, message));
        this.service = service;
        this.message = message;
    }
}
