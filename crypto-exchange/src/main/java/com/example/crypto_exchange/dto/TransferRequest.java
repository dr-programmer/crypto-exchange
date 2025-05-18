package com.example.crypto_exchange.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private String privateKey;
    private String recipientAddress;
    private BigDecimal amount;

    // Getters and Setters
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
} 