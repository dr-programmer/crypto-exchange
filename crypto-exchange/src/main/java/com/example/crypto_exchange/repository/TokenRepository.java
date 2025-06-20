package com.example.crypto_exchange.repository;

import com.example.crypto_exchange.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    
    /**
     * Find token by symbol (e.g., "ETH", "USDT")
     */
    Optional<Token> findBySymbol(String symbol);
    
    /**
     * Check if token exists by symbol
     */
    boolean existsBySymbol(String symbol);

    /**
     * Find token by its smart-contract address
     */
    Optional<Token> findByContractAddress(String contractAddress);
} 