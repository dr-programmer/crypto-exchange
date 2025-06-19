package com.example.crypto_exchange.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class TransferRequest {
    @NotNull(message = "From user ID is required")
    private Long fromUserId;
    
    @NotNull(message = "To user ID is required")
    private Long toUserId;
    
    @NotBlank(message = "Token symbol is required")
    private String tokenSymbol;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    // Constructors
    public TransferRequest() {}

    public TransferRequest(Long fromUserId, Long toUserId, String tokenSymbol, BigDecimal amount) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.tokenSymbol = tokenSymbol;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
} 