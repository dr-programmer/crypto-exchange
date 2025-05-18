package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.TransferRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    public String processTransfer(TransferRequest request) {
        // Log the transfer request for inspection
        log.info("Processing transfer request: from private key ending in ...{}, to address: {}, amount: {}",
                request.getPrivateKey().substring(request.getPrivateKey().length() - 4),
                request.getRecipientAddress(),
                request.getAmount());

        // Return mock transaction hash
        return "0x123abc";
    }
} 