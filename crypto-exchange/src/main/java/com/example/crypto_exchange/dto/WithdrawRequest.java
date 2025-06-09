package com.example.crypto_exchange.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class WithdrawRequest {
    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "Invalid user ID")
    private long userId;
    
    @NotBlank(message = "Token symbol is required")
    private String tokenSymbol;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Destination address is required")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "Invalid Ethereum address format")
    private String toAddress;

    public WithdrawRequest() {}

    public WithdrawRequest(long userId, String tokenSymbol, BigDecimal amount, String toAddress) {
        this.userId = userId;
        this.tokenSymbol = tokenSymbol;
        this.amount = amount;
        this.toAddress = toAddress;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
}
