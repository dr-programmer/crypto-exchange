package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.DepositRequest;
import com.example.crypto_exchange.entity.WalletBalance;
import com.example.crypto_exchange.repository.WalletBalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class DepositService {
    private static final Logger log = LoggerFactory.getLogger(DepositService.class);

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private WalletBalanceRepository walletBalanceRepository;

    @Transactional
    public String processDeposit(DepositRequest request) {
        // Generate a unique transaction ID
        String txId = UUID.randomUUID().toString();
        
        try {
            // Get the token balance from blockchain
            BigDecimal blockchainBalance = blockchainService.getTokenBalance(
                request.getTokenContractAddress(),
                request.getWalletAddress()
            );

            // Get or create wallet balance record
            WalletBalance walletBalance = walletBalanceRepository
                .findByWalletAddressAndTokenContractAddress(
                    request.getWalletAddress(),
                    request.getTokenContractAddress()
                )
                .orElse(new WalletBalance());

            // Update wallet balance
            walletBalance.setWalletAddress(request.getWalletAddress());
            walletBalance.setTokenContractAddress(request.getTokenContractAddress());
            walletBalance.setBalance(blockchainBalance);
            walletBalanceRepository.save(walletBalance);

            // Log the deposit
            log.info("Deposit processed - txId: {}, wallet: {}, token: {}, balance: {}",
                    txId, request.getWalletAddress(), request.getTokenContractAddress(), blockchainBalance);

            return txId;
        } catch (Exception e) {
            log.error("Error processing deposit for wallet {}: {}", request.getWalletAddress(), e.getMessage());
            throw new RuntimeException("Failed to process deposit", e);
        }
    }
} 