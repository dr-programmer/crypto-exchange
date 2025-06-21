package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.DepositRequest;
import com.example.crypto_exchange.entity.Token;
import com.example.crypto_exchange.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService {

    private final TokenRepository tokenRepository;
    private final UserBalanceService userBalanceService;

    /**
     * Process a deposit and add funds to user's balance
     */
    @Transactional
    public String processDeposit(DepositRequest request) {
        log.info("Processing deposit request: userId={}, tokenSymbol={}, amount={}, walletAddress={}", 
                request.getUserId(), request.getTokenSymbol(), request.getAmount(), request.getWalletAddress());

        // Input validation
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getWalletAddress() == null || request.getWalletAddress().isEmpty() || !request.getWalletAddress().matches("^0x[a-fA-F0-9]{40}$")) {
            throw new IllegalArgumentException("Invalid wallet address");
        }
        if (request.getTokenSymbol() == null || request.getTokenSymbol().isEmpty()) {
            throw new IllegalArgumentException("Token symbol cannot be empty");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Validate token exists
        Token token = tokenRepository.findBySymbol(request.getTokenSymbol())
            .orElseThrow(() -> new IllegalArgumentException("Token not found: " + request.getTokenSymbol()));

        // Add to user's balance
        userBalanceService.addToBalance(request.getUserId(), request.getTokenSymbol(), request.getAmount());

        log.info("Deposit processed successfully for user {}: {} {}", 
                request.getUserId(), request.getAmount(), request.getTokenSymbol());

        return String.format("Deposit of %s %s processed successfully", request.getAmount(), request.getTokenSymbol());
    }
} 