package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.WithdrawRequest;
import com.example.crypto_exchange.dto.WithdrawResponse;
import com.example.crypto_exchange.entity.Token;
import com.example.crypto_exchange.entity.UserBalance;
import com.example.crypto_exchange.exception.WithdrawException;
import com.example.crypto_exchange.repository.TokenRepository;
import com.example.crypto_exchange.repository.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.crypto.WalletUtils;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawService {
    
    private final TokenRepository tokenRepository;
    private final UserBalanceRepository userBalanceRepository;
    private final Web3j web3j;
    private final CredentialsFactory credentialsFactory;
    private final Executor executor = ForkJoinPool.commonPool();

    @Value("${exchange.wallet.private-key:dummy_private_key}")
    private String exchangeWalletPrivateKey;

    @Async
    @Transactional
    public CompletableFuture<WithdrawResponse> processWithdraw(WithdrawRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            validateRequest(request);
            log.info("Processing withdrawal request for user {} of {} {}", 
                    request.getUserId(), request.getAmount(), request.getTokenSymbol());

            Token token = tokenRepository.findBySymbol(request.getTokenSymbol())
                .orElseThrow(() -> new WithdrawException("TOKEN_NOT_FOUND", "Token not found"));

            UserBalance userBalance = userBalanceRepository.findByUserIdAndTokenId(request.getUserId(), token.getTokenId())
                .orElseThrow(() -> new WithdrawException("BALANCE_NOT_FOUND", "User balance not found"));

            if (!userBalance.hasSufficientBalance(request.getAmount())) {
                throw new WithdrawException("INSUFFICIENT_BALANCE", "Insufficient balance");
            }

            try {
                BigDecimal newAmount = userBalance.getAmount().subtract(request.getAmount());
                userBalanceRepository.updateBalanceAmount(request.getUserId(), token.getTokenId(), newAmount);

                String txHash = sendBlockchainTransaction(request.getToAddress(), request.getAmount());
                String txId = String.format("WD_%d_%s", request.getUserId(), System.currentTimeMillis());

                log.info("Withdrawal processed successfully. Transaction hash: {}", txHash);
                return new WithdrawResponse(txId, txHash, "SUCCESS");

            } catch (Exception e) {
                log.error("Error processing withdrawal: {}", e.getMessage(), e);
                throw new WithdrawException("BLOCKCHAIN_ERROR", "Failed to process withdrawal: " + e.getMessage());
            }
        }, executor);
    }

    // Simple synchronous version for testing
    @Transactional
    public WithdrawResponse processWithdrawSync(WithdrawRequest request) {
        log.info("Processing synchronous withdrawal request for user {} of {} {}", 
                request.getUserId(), request.getAmount(), request.getTokenSymbol());

        validateRequest(request);

        Token token = tokenRepository.findBySymbol(request.getTokenSymbol())
            .orElseThrow(() -> new WithdrawException("TOKEN_NOT_FOUND", "Token not found"));

        UserBalance userBalance = userBalanceRepository.findByUserIdAndTokenId(request.getUserId(), token.getTokenId())
            .orElseThrow(() -> new WithdrawException("BALANCE_NOT_FOUND", "User balance not found"));

        if (!userBalance.hasSufficientBalance(request.getAmount())) {
            throw new WithdrawException("INSUFFICIENT_BALANCE", "Insufficient balance");
        }

        try {
            BigDecimal newAmount = userBalance.getAmount().subtract(request.getAmount());
            userBalanceRepository.updateBalanceAmount(request.getUserId(), token.getTokenId(), newAmount);

            String txHash = sendBlockchainTransaction(request.getToAddress(), request.getAmount());
            String txId = String.format("WD_%d_%s", request.getUserId(), System.currentTimeMillis());

            log.info("Synchronous withdrawal processed successfully. Transaction hash: {}", txHash);
            return new WithdrawResponse(txId, txHash, "SUCCESS");

        } catch (Exception e) {
            log.error("Error processing synchronous withdrawal: {}", e.getMessage(), e);
            throw new WithdrawException("BLOCKCHAIN_ERROR", "Failed to process withdrawal: " + e.getMessage());
        }
    }

    private void validateRequest(WithdrawRequest request) {
        log.debug("Validating withdrawal request: userId={}, tokenSymbol={}, amount={}, toAddress={}", 
            request.getUserId(), request.getTokenSymbol(), request.getAmount(), request.getToAddress());
            
        if (request == null) {
            log.error("Withdrawal request is null");
            throw new WithdrawException("INVALID_REQUEST", "Request cannot be null");
        }
        if (request.getToAddress() == null || request.getToAddress().isEmpty() || !request.getToAddress().matches("^0x[a-fA-F0-9]{40}$")) {
            log.error("Invalid Ethereum address format: {}", request.getToAddress());
            throw new WithdrawException("INVALID_ADDRESS", "Invalid Ethereum address format");
        }
        if (request.getTokenSymbol() == null || request.getTokenSymbol().trim().isEmpty()) {
            log.error("Invalid token symbol: {}", request.getTokenSymbol());
            throw new WithdrawException("INVALID_TOKEN", "Token symbol cannot be empty");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid withdrawal amount: {}", request.getAmount());
            throw new WithdrawException("INVALID_AMOUNT", "Withdrawal amount must be positive");
        }
        log.debug("Withdrawal request validation successful");
    }

    @Retryable(
        value = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private String sendBlockchainTransaction(String toAddress, BigDecimal amount) throws Exception {
        log.debug("Sending blockchain transaction: toAddress={}, amount={}", toAddress, amount);
        
        // For testing purposes, return a mock transaction hash
        // In production, this would use real blockchain transactions
        if ("dummy_private_key".equals(exchangeWalletPrivateKey)) {
            log.info("Using mock blockchain transaction for testing");
            return "0x" + System.currentTimeMillis() + "mock_tx_hash_for_testing";
        }
        
        try {
            Credentials credentials = credentialsFactory.create(exchangeWalletPrivateKey);
            log.debug("Created credentials for address: {}", credentials.getAddress());
            
            RemoteCall<TransactionReceipt> remoteCall = Transfer.sendFunds(
                web3j, 
                credentials,
                toAddress,
                amount,
                Convert.Unit.ETHER
            );
            
            if (remoteCall == null) {
                log.error("Failed to create transaction: remoteCall is null");
                throw new WithdrawException("BLOCKCHAIN_ERROR", "Failed to create transaction");
            }
            
            TransactionReceipt receipt = remoteCall.send();
            if (receipt == null) {
                log.error("Failed to get transaction receipt: receipt is null");
                throw new WithdrawException("BLOCKCHAIN_ERROR", "Failed to get transaction receipt");
            }
            
            String txHash = receipt.getTransactionHash();
            log.info("Transaction successful. Hash: {}", txHash);
            return txHash;
        } catch (Exception e) {
            log.error("Error sending blockchain transaction: {}", e.getMessage(), e);
            throw new WithdrawException("BLOCKCHAIN_ERROR", "Failed to send transaction: " + e.getMessage());
        }
    }

    private void validateAddress(String address) {
        if (!WalletUtils.isValidAddress(address)) {
            throw new WithdrawException("INVALID_ADDRESS", "Invalid Ethereum address format");
        }
    }
}