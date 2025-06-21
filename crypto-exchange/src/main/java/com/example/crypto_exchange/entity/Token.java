package com.example.crypto_exchange.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
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

    @Column(name = "contract_address", length = 100)
    private String contractAddress;

    // Custom constructor
    public Token(String symbol, String name, Integer decimals) {
        this.symbol = symbol;
        this.name = name;
        this.decimals = decimals;
    }
} 