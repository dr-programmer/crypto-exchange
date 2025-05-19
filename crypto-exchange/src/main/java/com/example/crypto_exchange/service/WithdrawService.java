package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.WithdrawRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WithdrawService {

    private static final Logger log = LoggerFactory.getLogger(WithdrawService.class);

    /**
     * Simulate a withdrawal and return a human-friendly status message.
     * In a later sprint you’ll swap the body for a real web3j call.
     */
    public String processWithdraw(WithdrawRequest request) {

        log.info("Simulating withdraw -> wallet: {}, token: {}, amount: {}",
                request.getWalletAddress(), request.getToken(), request.getAmount());

        // 👉  anything you return here will bubble up to the controller
        return "Withdrawal simulated successfully";
    }
}
