package com.example.crypto_exchange.repository;

import com.example.crypto_exchange.entity.UserBalance;
import com.example.crypto_exchange.entity.UserBalanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, UserBalanceId> {
    
    /**
     * Find balance by user ID and token ID
     */
    Optional<UserBalance> findByUserIdAndTokenId(Long userId, Long tokenId);
    
    /**
     * Find all balances for a specific user
     */
    List<UserBalance> findByUserId(Long userId);
    
    /**
     * Find all balances for a specific token
     */
    List<UserBalance> findByTokenId(Long tokenId);
    
    /**
     * Find balances with amount greater than zero for a user
     */
    @Query("SELECT ub FROM UserBalance ub WHERE ub.userId = :userId AND ub.amount > 0")
    List<UserBalance> findNonZeroBalancesByUserId(@Param("userId") Long userId);
    
    /**
     * Update balance amount for a specific user-token combination
     */
    @Modifying
    @Query("UPDATE UserBalance ub SET ub.amount = :amount, ub.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE ub.userId = :userId AND ub.tokenId = :tokenId")
    int updateBalanceAmount(@Param("userId") Long userId, 
                           @Param("tokenId") Long tokenId, 
                           @Param("amount") BigDecimal amount);
    
    /**
     * Check if a balance record exists for user and token
     */
    boolean existsByUserIdAndTokenId(Long userId, Long tokenId);
    
    /**
     * Find balance with user and token entities loaded
     */
    @Query("SELECT ub FROM UserBalance ub " +
           "JOIN FETCH ub.user u " +
           "JOIN FETCH ub.token t " +
           "WHERE ub.userId = :userId AND ub.tokenId = :tokenId")
    Optional<UserBalance> findByUserIdAndTokenIdWithEntities(@Param("userId") Long userId, 
                                                           @Param("tokenId") Long tokenId);
    
    /**
     * Find all balances for a user with user and token entities loaded
     */
    @Query("SELECT ub FROM UserBalance ub " +
           "JOIN FETCH ub.user u " +
           "JOIN FETCH ub.token t " +
           "WHERE ub.userId = :userId")
    List<UserBalance> findByUserIdWithEntities(@Param("userId") Long userId);
} 