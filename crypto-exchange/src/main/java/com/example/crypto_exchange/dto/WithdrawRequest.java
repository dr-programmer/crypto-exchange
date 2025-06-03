package com.example.crypto_exchange.dto;

import java.math.BigDecimal;

public class WithdrawRequest {
    private long userId;
    private String tokenSymbol;
    private BigDecimal amount;
    private String toAddress;

    public WithdrawRequest(Long userId, String tokenSymbol, BigDecimal amount, String toAddress) {
        this.userId = userId;
        this.tokenSymbol = tokenSymbol;
        this.amount = amount;
        this.toAddress = toAddress;
    }

    public long getUserId() {
        return userId;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
