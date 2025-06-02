package com.example.crypto_exchange.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @NotBlank
    @Column(name = "symbol", length = 10, nullable = false, unique = true)
    private String symbol;

    @NotBlank
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @NotNull
    @Column(name = "decimals", nullable = false)
    private Integer decimals;

    // Constructors
    public Token() {}

    public Token(String symbol, String name, Integer decimals) {
        this.symbol = symbol;
        this.name = name;
        this.decimals = decimals;
    }

    // Getters and Setters
    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(tokenId, token.tokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenId);
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenId=" + tokenId +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", decimals=" + decimals +
                '}';
    }
} 