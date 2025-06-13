package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.WithdrawRequest;
import com.example.crypto_exchange.dto.WithdrawResponse;
import com.example.crypto_exchange.entity.Token;
import com.example.crypto_exchange.entity.TransactionType;
import com.example.crypto_exchange.entity.UserBalance;
import com.example.crypto_exchange.exception.WithdrawException;
import com.example.crypto_exchange.repository.TokenRepository;
import com.example.crypto_exchange.repository.UserBalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class WithdrawService {

    private static final Logger log = LoggerFactory.getLogger(WithdrawService.class);
    
    private final TokenRepository tokenRepository;
    private final UserBalanceRepository userBalanceRepository;
    private final TransactionLogService transactionLogService;
    private final Web3j web3j;

    @Value("${exchange.wallet.private-key}")
    private String exchangeWalletPrivateKey;

    @Autowired
    public WithdrawService(
            TokenRepository tokenRepository, 
            UserBalanceRepository userBalanceRepository,
            TransactionLogService transactionLogService,
            Web3j web3j) {
        this.tokenRepository = tokenRepository;
        this.userBalanceRepository = userBalanceRepository;
        this.transactionLogService = transactionLogService;
        this.web3j = web3j;
    }

    @Async
    @Transactional
    public CompletableFuture<WithdrawResponse> processWithdraw(WithdrawRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            validateRequest(request);
            log.info("Processing withdrawal request for user {} of {} {}", 
                    request.getUserId(), request.getAmount(), request.getTokenSymbol());

            Token token = tokenRepository.findBySymbol(request.getTokenSymbol())
                .orElseThrow(() -> new WithdrawException("Token not found", "TOKEN_NOT_FOUND"));

            UserBalance userBalance = userBalanceRepository.findByUserIdAndTokenId(request.getUserId(), token.getTokenId())
                .orElseThrow(() -> new WithdrawException("User balance not found", "BALANCE_NOT_FOUND"));

            if (!userBalance.hasSufficientBalance(request.getAmount())) {
                String errorMsg = "Insufficient balance";
                transactionLogService.logFailedTransaction(
                    TransactionType.WITHDRAW,
                    request.getUserId(),
                    request.getTokenSymbol(),
                    request.getAmount(),
                    errorMsg
                );
                throw new WithdrawException(errorMsg, "INSUFFICIENT_BALANCE");
            }

            try {
                BigDecimal newAmount = userBalance.getAmount().subtract(request.getAmount());
                userBalanceRepository.updateBalanceAmount(request.getUserId(), token.getTokenId(), newAmount);

                String txHash = sendBlockchainTransaction(request.getToAddress(), request.getAmount());
                String txId = String.format("WD_%d_%s", request.getUserId(), System.currentTimeMillis());

                // Log the successful withdrawal
                transactionLogService.logWithdraw(
                    request.getUserId(),
                    request.getTokenSymbol(),
                    request.getAmount(),
                    request.getToAddress(),
                    txHash
                );

                log.info("Withdrawal processed successfully. Transaction hash: {}", txHash);
                return new WithdrawResponse(txId, txHash, "SUCCESS");

            } catch (Exception e) {
                log.error("Error processing withdrawal: {}", e.getMessage(), e);
                
                // Log the failed transaction
                transactionLogService.logFailedTransaction(
                    TransactionType.WITHDRAW,
                    request.getUserId(),
                    request.getTokenSymbol(),
                    request.getAmount(),
                    e.getMessage()
                );
                
                throw new WithdrawException("Failed to process withdrawal: " + e.getMessage(), "BLOCKCHAIN_ERROR");
            }
        });
    }

    private void validateRequest(WithdrawRequest request) {
        if (request == null) {
            throw new WithdrawException("Request cannot be null", "INVALID_REQUEST");
        }
        if (request.getToAddress() == null || request.getToAddress().isEmpty() || !request.getToAddress().matches("^0x[a-fA-F0-9]{40}$")) {
            throw new WithdrawException("Invalid Ethereum address format", "INVALID_ADDRESS");
        }
        if (request.getTokenSymbol() == null || request.getTokenSymbol().trim().isEmpty() ||
            !(request.getTokenSymbol().equals("ETH") || request.getTokenSymbol().equals("USDC"))) {
            throw new WithdrawException("Unsupported or empty token symbol", "INVALID_TOKEN");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new WithdrawException("Withdrawal amount must be positive", "INVALID_AMOUNT");
        }
    }

    @Retryable(
        value = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private String sendBlockchainTransaction(String toAddress, BigDecimal amount) throws Exception {
        Credentials credentials = Credentials.create(exchangeWalletPrivateKey);
        TransactionReceipt receipt = Transfer.sendFunds(
            web3j, 
            credentials,
            toAddress,
            amount,
            Convert.Unit.ETHER
        ).send();
        
        return receipt.getTransactionHash();
    }
} 