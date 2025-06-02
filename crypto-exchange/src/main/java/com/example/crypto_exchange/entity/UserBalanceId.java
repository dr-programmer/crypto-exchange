package com.example.crypto_exchange.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for UserBalance entity.
 * This represents the composite key (user_id, token_id) from the balances table.
 */
public class UserBalanceId implements Serializable {

    private Long userId;
    private Long tokenId;

    // Default constructor
    public UserBalanceId() {}

    public UserBalanceId(Long userId, Long tokenId) {
        this.userId = userId;
        this.tokenId = tokenId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBalanceId that = (UserBalanceId) o;
        return Objects.equals(userId, that.userId) && 
               Objects.equals(tokenId, that.tokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tokenId);
    }

    @Override
    public String toString() {
        return "UserBalanceId{" +
                "userId=" + userId +
                ", tokenId=" + tokenId +
                '}';
    }
} 