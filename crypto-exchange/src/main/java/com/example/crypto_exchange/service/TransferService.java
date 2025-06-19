package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.TransferRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {
    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    @Autowired
    private UserBalanceService userBalanceService;

    /**
     * Process a transfer between two users
     */
    @Transactional
    public String processTransfer(TransferRequest request) {
        log.info("Processing transfer request: fromUserId={}, toUserId={}, tokenSymbol={}, amount={}", 
                request.getFromUserId(), request.getToUserId(), request.getTokenSymbol(), request.getAmount());

        // Input validation
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getFromUserId() == null || request.getFromUserId() <= 0) {
            throw new IllegalArgumentException("Invalid from user ID");
        }
        if (request.getToUserId() == null || request.getToUserId() <= 0) {
            throw new IllegalArgumentException("Invalid to user ID");
        }
        if (request.getFromUserId().equals(request.getToUserId())) {
            throw new IllegalArgumentException("Cannot transfer to the same user");
        }
        if (request.getTokenSymbol() == null || request.getTokenSymbol().isEmpty()) {
            throw new IllegalArgumentException("Token symbol cannot be empty");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Check if sender has sufficient balance
        if (!userBalanceService.hasSufficientBalance(request.getFromUserId(), request.getTokenSymbol(), request.getAmount())) {
            throw new IllegalArgumentException("Insufficient balance for transfer");
        }

        // Process the transfer
        userBalanceService.transferBalance(request.getFromUserId(), request.getToUserId(), request.getTokenSymbol(), request.getAmount());

        // Generate mock transaction hash
        String txHash = "0x" + System.currentTimeMillis() + "transfer_hash";

        log.info("Transfer processed successfully: {} {} from user {} to user {}, txHash: {}", 
                request.getAmount(), request.getTokenSymbol(), request.getFromUserId(), request.getToUserId(), txHash);

        return txHash;
    }
} 