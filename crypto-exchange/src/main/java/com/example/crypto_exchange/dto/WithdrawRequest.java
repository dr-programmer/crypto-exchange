package com.example.crypto_exchange.dto;

import java.math.BigDecimal;

public class WithdrawRequest {

    private String walletAddress;   // the user’s address on L2 / EVM
    private String token;           // e.g. “ETH”, “USDC”
    private BigDecimal amount;      // human-readable units, not wei

    // ───────── getters / setters ─────────
    public String getWalletAddress() { return walletAddress; }
    public void setWalletAddress(String walletAddress) { this.walletAddress = walletAddress; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
