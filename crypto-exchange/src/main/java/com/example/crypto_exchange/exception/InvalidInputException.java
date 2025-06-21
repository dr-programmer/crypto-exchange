package com.example.crypto_exchange.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidInputException extends RuntimeException {
    
    public InvalidInputException(String message) {
        super(message);
        log.error("InvalidInputException created - Message: {}", message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
        log.error("InvalidInputException created with cause - Message: {}, Cause: {}", message, cause.getMessage());
    }
} 