package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.DepositRequest;
import com.example.crypto_exchange.entity.TransactionType;
import com.example.crypto_exchange.exception.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepositService {
    private static final Logger log = LoggerFactory.getLogger(DepositService.class);

    private final TransactionLogService transactionLogService;

    @Autowired
    public DepositService(TransactionLogService transactionLogService) {
        this.transactionLogService = transactionLogService;
    }

    /**
     * Process a deposit and log the transaction
     */
    @Transactional
    public String processDeposit(DepositRequest request) {
        // Input validation
        if (request == null) {
            throw new InvalidInputException("Request cannot be null");
        }
        if (request.getWalletAddress() == null || request.getWalletAddress().isEmpty() || !request.getWalletAddress().matches("^0x[a-fA-F0-9]{40}$")) {
            throw new InvalidInputException("Invalid wallet address");
        }
        if (request.getToken() == null || request.getToken().isEmpty() || !(request.getToken().equals("ETH") || request.getToken().equals("USDC"))) {
            throw new InvalidInputException("Unsupported or empty token");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Amount must be positive");
        }

        try {
            log.info("Processing deposit -> wallet: {}, token: {}, amount: {}",
                    request.getWalletAddress(), request.getToken(), request.getAmount());

            // TODO: In a later sprint, implement actual blockchain deposit verification
            String mockTxHash = "0x" + java.util.UUID.randomUUID().toString().replace("-", "");

            // Log the successful deposit
            transactionLogService.logDeposit(
                1L, // TODO: Get actual user ID from authentication context
                request.getToken(),
                request.getAmount(),
                request.getWalletAddress(),
                mockTxHash
            );

            return "Deposit processed successfully. Transaction hash: " + mockTxHash;
        } catch (Exception e) {
            log.error("Error processing deposit: {}", e.getMessage(), e);
            
            // Log the failed transaction
            transactionLogService.logFailedTransaction(
                TransactionType.DEPOSIT,
                1L, // TODO: Get actual user ID from authentication context
                request.getToken(),
                request.getAmount(),
                e.getMessage()
            );
            
            throw new InvalidInputException("Failed to process deposit: " + e.getMessage());
        }
    }
} 