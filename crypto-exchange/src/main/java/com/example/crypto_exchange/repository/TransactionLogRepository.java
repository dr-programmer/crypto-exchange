package com.example.crypto_exchange.repository;

import com.example.crypto_exchange.entity.TransactionLog;
import com.example.crypto_exchange.entity.TransactionStatus;
import com.example.crypto_exchange.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    
    /**
     * Find all transactions for a specific user
     */
    Page<TransactionLog> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find all transactions for a specific user and transaction type
     */
    Page<TransactionLog> findByUserIdAndTransactionType(Long userId, TransactionType type, Pageable pageable);
    
    /**
     * Find all transactions for a specific user and status
     */
    Page<TransactionLog> findByUserIdAndStatus(Long userId, TransactionStatus status, Pageable pageable);
    
    /**
     * Find all transactions for a specific token
     */
    Page<TransactionLog> findByTokenId(Long tokenId, Pageable pageable);
    
    /**
     * Find transactions by transaction hash
     */
    List<TransactionLog> findByTxHash(String txHash);
    
    /**
     * Find transactions within a time range
     */
    @Query("SELECT t FROM TransactionLog t WHERE t.createdAt BETWEEN :startTime AND :endTime")
    Page<TransactionLog> findByTimeRange(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        Pageable pageable
    );
    
    /**
     * Find transactions by user and time range
     */
    @Query("SELECT t FROM TransactionLog t WHERE t.userId = :userId AND t.createdAt BETWEEN :startTime AND :endTime")
    Page<TransactionLog> findByUserIdAndTimeRange(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        Pageable pageable
    );
    
    /**
     * Find failed transactions for a user
     */
    @Query("SELECT t FROM TransactionLog t WHERE t.userId = :userId AND t.status = 'FAILED'")
    List<TransactionLog> findFailedTransactionsByUserId(@Param("userId") Long userId);
} 