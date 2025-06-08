package com.example.crypto_exchange.service;

import com.example.crypto_exchange.entity.Token;
import com.example.crypto_exchange.entity.User;
import com.example.crypto_exchange.entity.UserBalance;
import com.example.crypto_exchange.entity.UserBalanceId;
import com.example.crypto_exchange.repository.TokenRepository;
import com.example.crypto_exchange.repository.UserBalanceRepository;
import com.example.crypto_exchange.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserBalanceService {

    private static final Logger log = LoggerFactory.getLogger(UserBalanceService.class);

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    /**
     * Get balance for a specific user and token.
     * Returns BigDecimal.ZERO if no balance exists.
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId, Long tokenId) {
        log.debug("Getting balance for user {} and token {}", userId, tokenId);
        
        return userBalanceRepository.findByUserIdAndTokenId(userId, tokenId)
                .map(UserBalance::getAmount)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Get balance for a specific user and token by symbol.
     * Returns BigDecimal.ZERO if no balance exists.
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId, String tokenSymbol) {
        log.debug("Getting balance for user {} and token symbol {}", userId, tokenSymbol);
        
        Optional<Token> token = tokenRepository.findBySymbol(tokenSymbol);
        if (token.isEmpty()) {
            log.warn("Token with symbol {} not found", tokenSymbol);
            return BigDecimal.ZERO;
        }
        
        return getBalance(userId, token.get().getTokenId());
    }

    /**
     * Get all balances for a specific user
     */
    @Transactional(readOnly = true)
    public List<UserBalance> getAllBalances(Long userId) {
        log.debug("Getting all balances for user {}", userId);
        return userBalanceRepository.findByUserIdWithEntities(userId);
    }

    /**
     * Get all non-zero balances for a specific user
     */
    @Transactional(readOnly = true)
    public List<UserBalance> getNonZeroBalances(Long userId) {
        log.debug("Getting non-zero balances for user {}", userId);
        return userBalanceRepository.findNonZeroBalancesByUserId(userId);
    }

    /**
     * Set balance for a specific user and token.
     * Creates a new balance record if it doesn't exist.
     */
    public UserBalance setBalance(Long userId, Long tokenId, BigDecimal amount) {
        log.info("Setting balance for user {} and token {} to {}", userId, tokenId, amount);
        
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        
        // Validate that user and token exist
        validateUserExists(userId);
        validateTokenExists(tokenId);
        
        Optional<UserBalance> existingBalance = userBalanceRepository.findByUserIdAndTokenId(userId, tokenId);
        
        if (existingBalance.isPresent()) {
            UserBalance balance = existingBalance.get();
            balance.setAmount(amount);
            return userBalanceRepository.save(balance);
        } else {
            UserBalance newBalance = new UserBalance(userId, tokenId, amount);
            return userBalanceRepository.save(newBalance);
        }
    }

    /**
     * Set balance for a specific user and token by symbol.
     */
    public UserBalance setBalance(Long userId, String tokenSymbol, BigDecimal amount) {
        log.info("Setting balance for user {} and token symbol {} to {}", userId, tokenSymbol, amount);
        
        Token token = getTokenBySymbolOrThrow(tokenSymbol);
        return setBalance(userId, token.getTokenId(), amount);
    }

    /**
     * Add amount to existing balance.
     * Creates a new balance record with the amount if it doesn't exist.
     */
    public UserBalance addToBalance(Long userId, Long tokenId, BigDecimal amountToAdd) {
        log.info("Adding {} to balance for user {} and token {}", amountToAdd, userId, tokenId);
        
        if (amountToAdd == null || amountToAdd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive");
        }
        
        // Validate that user and token exist
        validateUserExists(userId);
        validateTokenExists(tokenId);
        
        Optional<UserBalance> existingBalance = userBalanceRepository.findByUserIdAndTokenId(userId, tokenId);
        
        if (existingBalance.isPresent()) {
            UserBalance balance = existingBalance.get();
            balance.addAmount(amountToAdd);
            return userBalanceRepository.save(balance);
        } else {
            UserBalance newBalance = new UserBalance(userId, tokenId, amountToAdd);
            return userBalanceRepository.save(newBalance);
        }
    }

    /**
     * Add amount to existing balance by token symbol.
     */
    public UserBalance addToBalance(Long userId, String tokenSymbol, BigDecimal amountToAdd) {
        log.info("Adding {} to balance for user {} and token symbol {}", amountToAdd, userId, tokenSymbol);
        
        Token token = getTokenBySymbolOrThrow(tokenSymbol);
        return addToBalance(userId, token.getTokenId(), amountToAdd);
    }

    /**
     * Subtract amount from existing balance.
     * Throws exception if insufficient balance.
     */
    public UserBalance subtractFromBalance(Long userId, Long tokenId, BigDecimal amountToSubtract) {
        log.info("Subtracting {} from balance for user {} and token {}", amountToSubtract, userId, tokenId);
        
        if (amountToSubtract == null || amountToSubtract.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount to subtract must be positive");
        }
        
        UserBalance balance = userBalanceRepository.findByUserIdAndTokenId(userId, tokenId)
                .orElseThrow(() -> new IllegalStateException("No balance found for user " + userId + " and token " + tokenId));
        
        if (!balance.hasSufficientBalance(amountToSubtract)) {
            throw new IllegalStateException("Insufficient balance. Current: " + balance.getAmount() + ", Required: " + amountToSubtract);
        }
        
        balance.subtractAmount(amountToSubtract);
        return userBalanceRepository.save(balance);
    }

    /**
     * Subtract amount from existing balance by token symbol.
     */
    public UserBalance subtractFromBalance(Long userId, String tokenSymbol, BigDecimal amountToSubtract) {
        log.info("Subtracting {} from balance for user {} and token symbol {}", amountToSubtract, userId, tokenSymbol);
        
        Token token = getTokenBySymbolOrThrow(tokenSymbol);
        return subtractFromBalance(userId, token.getTokenId(), amountToSubtract);
    }

    /**
     * Check if user has sufficient balance for a transaction
     */
    @Transactional(readOnly = true)
    public boolean hasSufficientBalance(Long userId, Long tokenId, BigDecimal requiredAmount) {
        if (requiredAmount == null || requiredAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        
        BigDecimal currentBalance = getBalance(userId, tokenId);
        return currentBalance.compareTo(requiredAmount) >= 0;
    }

    /**
     * Check if user has sufficient balance by token symbol
     */
    @Transactional(readOnly = true)
    public boolean hasSufficientBalance(Long userId, String tokenSymbol, BigDecimal requiredAmount) {
        Token token = getTokenBySymbolOrThrow(tokenSymbol);
        return hasSufficientBalance(userId, token.getTokenId(), requiredAmount);
    }

    /**
     * Transfer balance between two users for the same token
     */
    public void transferBalance(Long fromUserId, Long toUserId, Long tokenId, BigDecimal amount) {
        log.info("Transferring {} of token {} from user {} to user {}", amount, tokenId, fromUserId, toUserId);
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        // Validate users and token exist
        validateUserExists(fromUserId);
        validateUserExists(toUserId);
        validateTokenExists(tokenId);
        
        // Subtract from sender
        subtractFromBalance(fromUserId, tokenId, amount);
        
        // Add to receiver
        addToBalance(toUserId, tokenId, amount);
        
        log.info("Transfer completed successfully");
    }

    /**
     * Transfer balance between two users by token symbol
     */
    public void transferBalance(Long fromUserId, Long toUserId, String tokenSymbol, BigDecimal amount) {
        Token token = getTokenBySymbolOrThrow(tokenSymbol);
        transferBalance(fromUserId, toUserId, token.getTokenId(), amount);
    }

    // Helper methods
    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }
    }

    private void validateTokenExists(Long tokenId) {
        if (!tokenRepository.existsById(tokenId)) {
            throw new IllegalArgumentException("Token with ID " + tokenId + " does not exist");
        }
    }

    private Token getTokenBySymbolOrThrow(String tokenSymbol) {
        return tokenRepository.findBySymbol(tokenSymbol)
                .orElseThrow(() -> new IllegalArgumentException("Token with symbol " + tokenSymbol + " does not exist"));
    }
} 