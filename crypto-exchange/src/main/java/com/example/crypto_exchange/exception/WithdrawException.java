package com.example.crypto_exchange.exception;

public class WithdrawException extends RuntimeException {
    private final String errorCode;
    public WithdrawException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public WithdrawException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public String getErrorCode() {
        return errorCode;
    }
} 