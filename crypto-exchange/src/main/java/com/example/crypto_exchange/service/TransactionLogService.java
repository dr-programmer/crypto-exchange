package com.example.crypto_exchange.service;

import com.example.crypto_exchange.entity.*;
import com.example.crypto_exchange.repository.TransactionLogRepository;
import com.example.crypto_exchange.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionLogService {
    private static final Logger log = LoggerFactory.getLogger(TransactionLogService.class);

    private final TransactionLogRepository transactionLogRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public TransactionLogService(
            TransactionLogRepository transactionLogRepository,
            TokenRepository tokenRepository) {
        this.transactionLogRepository = transactionLogRepository;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Log a deposit transaction
     */
    @Transactional
    public TransactionLog logDeposit(Long userId, String tokenSymbol, BigDecimal amount, String fromAddress, String txHash) {
        Token token = tokenRepository.findBySymbol(tokenSymbol)
                .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenSymbol));

        TransactionLog logEntry = new TransactionLog();
        logEntry.setTransactionType(TransactionType.DEPOSIT);
        logEntry.setUserId(userId);
        logEntry.setToken(token);
        logEntry.setAmount(amount);
        logEntry.setFromAddress(fromAddress);
        logEntry.setTxHash(txHash);
        logEntry.setStatus(TransactionStatus.COMPLETED);

        TransactionLog savedLog = transactionLogRepository.save(logEntry);
        log.info("Deposit logged: userId={}, token={}, amount={}, txHash={}", 
                userId, tokenSymbol, amount, txHash);
        return savedLog;
    }

    /**
     * Log a withdrawal transaction
     */
    @Transactional
    public TransactionLog logWithdraw(Long userId, String tokenSymbol, BigDecimal amount, String toAddress, String txHash) {
        Token token = tokenRepository.findBySymbol(tokenSymbol)
                .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenSymbol));

        TransactionLog logEntry = new TransactionLog();
        logEntry.setTransactionType(TransactionType.WITHDRAW);
        logEntry.setUserId(userId);
        logEntry.setToken(token);
        logEntry.setAmount(amount);
        logEntry.setToAddress(toAddress);
        logEntry.setTxHash(txHash);
        logEntry.setStatus(TransactionStatus.COMPLETED);

        TransactionLog savedLog = transactionLogRepository.save(logEntry);
        log.info("Withdrawal logged: userId={}, token={}, amount={}, txHash={}", 
                userId, tokenSymbol, amount, txHash);
        return savedLog;
    }

    /**
     * Log a transfer transaction
     */
    @Transactional
    public TransactionLog logTransfer(Long fromUserId, Long toUserId, String tokenSymbol, BigDecimal amount, String txHash) {
        Token token = tokenRepository.findBySymbol(tokenSymbol)
                .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenSymbol));

        TransactionLog logEntry = new TransactionLog();
        logEntry.setTransactionType(TransactionType.TRANSFER);
        logEntry.setUserId(fromUserId); // Log from the sender's perspective
        logEntry.setToken(token);
        logEntry.setAmount(amount);
        logEntry.setToAddress(String.valueOf(toUserId)); // Store recipient user ID in toAddress
        logEntry.setTxHash(txHash);
        logEntry.setStatus(TransactionStatus.COMPLETED);

        TransactionLog savedLog = transactionLogRepository.save(logEntry);
        log.info("Transfer logged: fromUserId={}, toUserId={}, token={}, amount={}, txHash={}", 
                fromUserId, toUserId, tokenSymbol, amount, txHash);
        return savedLog;
    }

    /**
     * Log a failed transaction
     */
    @Transactional
    public TransactionLog logFailedTransaction(
            TransactionType type,
            Long userId,
            String tokenSymbol,
            BigDecimal amount,
            String errorMessage) {
        
        Token token = tokenRepository.findBySymbol(tokenSymbol)
                .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenSymbol));

        TransactionLog logEntry = new TransactionLog();
        logEntry.setTransactionType(type);
        logEntry.setUserId(userId);
        logEntry.setToken(token);
        logEntry.setAmount(amount);
        logEntry.setStatus(TransactionStatus.FAILED);
        logEntry.setErrorMessage(errorMessage);

        TransactionLog savedLog = transactionLogRepository.save(logEntry);
        log.error("Failed transaction logged: type={}, userId={}, token={}, amount={}, error={}", 
                type, userId, tokenSymbol, amount, errorMessage);
        return savedLog;
    }

    /**
     * Update transaction status
     */
    @Transactional
    public TransactionLog updateTransactionStatus(Long logId, TransactionStatus newStatus, String errorMessage) {
        TransactionLog logEntry = transactionLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction log not found: " + logId));

        logEntry.setStatus(newStatus);
        if (errorMessage != null) {
            logEntry.setErrorMessage(errorMessage);
        }

        TransactionLog updatedLog = transactionLogRepository.save(logEntry);
        log.info("Transaction status updated: logId={}, newStatus={}", logId, newStatus);
        return updatedLog;
    }

    /**
     * Get transaction history for a user
     */
    public Page<TransactionLog> getUserTransactionHistory(Long userId, Pageable pageable) {
        return transactionLogRepository.findByUserId(userId, pageable);
    }

    /**
     * Get transaction history for a user by type
     */
    public Page<TransactionLog> getUserTransactionHistoryByType(Long userId, TransactionType type, Pageable pageable) {
        return transactionLogRepository.findByUserIdAndTransactionType(userId, type, pageable);
    }

    /**
     * Get failed transactions for a user
     */
    public List<TransactionLog> getFailedTransactions(Long userId) {
        return transactionLogRepository.findFailedTransactionsByUserId(userId);
    }

    /**
     * Get transactions within a time range
     */
    public Page<TransactionLog> getTransactionsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return transactionLogRepository.findByTimeRange(startTime, endTime, pageable);
    }
} 