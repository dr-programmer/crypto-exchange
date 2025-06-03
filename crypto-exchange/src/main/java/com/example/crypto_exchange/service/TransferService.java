package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.TransferRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    public String processTransfer(TransferRequest request) {
        // Input validation
        if (request == null) {
            throw new com.example.crypto_exchange.exception.InvalidInputException("Request cannot be null");
        }
        if (request.getPrivateKey() == null || request.getPrivateKey().isEmpty() || !request.getPrivateKey().matches("^0x[a-fA-F0-9]{64}$")) {
            throw new com.example.crypto_exchange.exception.InvalidInputException("Invalid private key");
        }
        if (request.getRecipientAddress() == null || request.getRecipientAddress().isEmpty() || !request.getRecipientAddress().matches("^0x[a-fA-F0-9]{40}$")) {
            throw new com.example.crypto_exchange.exception.InvalidInputException("Invalid recipient address");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new com.example.crypto_exchange.exception.InvalidInputException("Amount must be positive");
        }
        // Log the transfer request for inspection
        log.info("Processing transfer request: from private key ending in ...{}, to address: {}, amount: {}",
                request.getPrivateKey().substring(request.getPrivateKey().length() - 4),
                request.getRecipientAddress(),
                request.getAmount());

        // Return mock transaction hash
        return "0x123abc";
    }
} 