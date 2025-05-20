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
        log.info("Simulating deposit -> wallet: {}, token: {}, amount: {}",
                request.getWalletAddress(), request.getToken(), request.getAmount());
        return "Deposit simulated successfully";
    }
} 