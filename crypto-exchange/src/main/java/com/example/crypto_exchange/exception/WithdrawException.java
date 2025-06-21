package com.example.crypto_exchange.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WithdrawException extends RuntimeException {
    
    private final String errorCode;
    
    public WithdrawException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        log.error("WithdrawException created - ErrorCode: {}, Message: {}", errorCode, message);
    }

    public WithdrawException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        log.error("WithdrawException created with cause - Message: {}, Cause: {}", message, cause.getMessage());
    }

    public String getErrorCode() {
        return errorCode;
    }
} 