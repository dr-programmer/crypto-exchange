package com.example.crypto_exchange.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DepositRequest {
    @NotBlank(message = "Wallet address is required")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "Invalid wallet address format")
    private String walletAddress;   // the user's address on L2 / EVM

    @NotBlank(message = "Token contract address is required")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "Invalid token contract address format")
    private String tokenContractAddress;

    private String token;           // e.g. "ETH", "USDC"
    private BigDecimal amount;      // human-readable units, not wei

    // ───────── getters / setters ─────────
    public String getWalletAddress() { return walletAddress; }
    public void setWalletAddress(String walletAddress) { this.walletAddress = walletAddress; }

    public String getTokenContractAddress() { return tokenContractAddress; }
    public void setTokenContractAddress(String tokenContractAddress) { this.tokenContractAddress = tokenContractAddress; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
} 