package com.example.crypto_exchange.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "balances")
@IdClass(UserBalanceId.class)
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserBalance {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "token_id")
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", insertable = false, updatable = false)
    private Token token;

    @NotNull
    @Column(name = "amount", precision = 36, scale = 18, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Custom constructors
    public UserBalance(Long userId, Long tokenId, BigDecimal amount) {
        this.userId = userId;
        this.tokenId = tokenId;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }

    public UserBalance(User user, Token token, BigDecimal amount) {
        this.userId = user.getUserId();
        this.tokenId = token.getTokenId();
        this.user = user;
        this.token = token;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }

    // Custom setters for JPA relationships
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getUserId();
        }
    }

    public void setToken(Token token) {
        this.token = token;
        if (token != null) {
            this.tokenId = token.getTokenId();
        }
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods for balance operations
    public void addAmount(BigDecimal amountToAdd) {
        if (amountToAdd != null && amountToAdd.compareTo(BigDecimal.ZERO) > 0) {
            this.amount = this.amount.add(amountToAdd);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void subtractAmount(BigDecimal amountToSubtract) {
        if (amountToSubtract != null && amountToSubtract.compareTo(BigDecimal.ZERO) > 0) {
            this.amount = this.amount.subtract(amountToSubtract);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean hasSufficientBalance(BigDecimal requiredAmount) {
        return requiredAmount != null && 
               this.amount.compareTo(requiredAmount) >= 0;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.amount == null) {
            this.amount = BigDecimal.ZERO;
        }
    }
} 