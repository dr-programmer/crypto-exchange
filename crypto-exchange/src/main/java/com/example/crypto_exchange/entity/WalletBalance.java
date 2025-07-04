package com.example.crypto_exchange.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wallet_balances", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"wallet_address", "token_contract_address"})
})
public class WalletBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_address", nullable = false)
    private String walletAddress;

    @Column(name = "token_contract_address", nullable = false)
    private String tokenContractAddress;

    @Column(nullable = false)
    private BigDecimal balance;

    /**
     * Internal user that owns this wallet address. This allows the deposit watcher
     * to credit the correct account when on-chain funds arrive.
     */
    @Column(name = "user_id")
    private Long userId;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getTokenContractAddress() {
        return tokenContractAddress;
    }

    public void setTokenContractAddress(String tokenContractAddress) {
        this.tokenContractAddress = tokenContractAddress;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
} 