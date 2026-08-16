package com.climateplatform.ai.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AIPromptException extends RuntimeException {
    private final String message;

    public AIPromptException(String message) {
        super(message);
        this.message = message;
    }
}
