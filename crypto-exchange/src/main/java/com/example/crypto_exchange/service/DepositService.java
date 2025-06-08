package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.DepositRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DepositService {
    private static final Logger log = LoggerFactory.getLogger(DepositService.class);

    /**
     * Simulate a deposit and return a human-friendly status message.
     * In a later sprint you'll swap the body for a real web3j call.
     */
    public String processDeposit(DepositRequest request) {
        // Input validation
        if (request == null) {
            throw new com.example.crypto_exchange.exception.InvalidInputException("Request cannot be null");
        }
        if (request.getWalletAddress() == null || request.getWalletAddress().isEmpty() || !request.getWalletAddress().matches("^0x[a-fA-F0-9]{40}$")) {
            throw new com.example.crypto_exchange.exception.InvalidInputException("Invalid wallet address");
        }
        if (request.getToken() == null || request.getToken().isEmpty() || !(request.getToken().equals("ETH") || request.getToken().equals("USDC"))) {
            throw new com.example.crypto_exchange.exception.InvalidInputException("Unsupported or empty token");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new com.example.crypto_exchange.exception.InvalidInputException("Amount must be positive");
        }
        log.info("Simulating deposit -> wallet: {}, token: {}, amount: {}",
                request.getWalletAddress(), request.getToken(), request.getAmount());
        return "Deposit simulated successfully";
    }
} 