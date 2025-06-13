package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.TransferRequest;
import com.example.crypto_exchange.entity.TransactionType;
import com.example.crypto_exchange.exception.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {
    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final TransactionLogService transactionLogService;
    private final UserBalanceService userBalanceService;

    @Autowired
    public TransferService(
            TransactionLogService transactionLogService,
            UserBalanceService userBalanceService) {
        this.transactionLogService = transactionLogService;
        this.userBalanceService = userBalanceService;
    }

    @Transactional
    public String processTransfer(TransferRequest request) {
        // Input validation
        if (request == null) {
            throw new InvalidInputException("Request cannot be null");
        }
        if (request.getPrivateKey() == null || request.getPrivateKey().isEmpty() || !request.getPrivateKey().matches("^0x[a-fA-F0-9]{64}$")) {
            throw new InvalidInputException("Invalid private key");
        }
        if (request.getRecipientAddress() == null || request.getRecipientAddress().isEmpty() || !request.getRecipientAddress().matches("^0x[a-fA-F0-9]{40}$")) {
            throw new InvalidInputException("Invalid recipient address");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Amount must be positive");
        }

        try {
            // TODO: Get actual user IDs from authentication and address lookup
            Long fromUserId = 1L; // Mock sender ID
            Long toUserId = 2L;   // Mock recipient ID
            String tokenSymbol = "ETH"; // Mock token

            // Log the transfer request
            log.info("Processing transfer request: from user {}, to address: {}, amount: {}",
                    fromUserId, request.getRecipientAddress(), request.getAmount());

            // TODO: Implement actual blockchain transfer
            String mockTxHash = "0x" + java.util.UUID.randomUUID().toString().replace("-", "");

            // Log the successful transfer
            transactionLogService.logTransfer(
                fromUserId,
                toUserId,
                tokenSymbol,
                request.getAmount(),
                mockTxHash
            );

            return mockTxHash;
        } catch (Exception e) {
            log.error("Error processing transfer: {}", e.getMessage(), e);
            
            // Log the failed transaction
            transactionLogService.logFailedTransaction(
                TransactionType.TRANSFER,
                1L, // TODO: Get actual user ID from authentication
                "ETH", // TODO: Get actual token from request
                request.getAmount(),
                e.getMessage()
            );
            
            throw new InvalidInputException("Failed to process transfer: " + e.getMessage());
        }
    }
} 