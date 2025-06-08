package com.example.crypto_exchange.service;

import com.example.crypto_exchange.entity.Token;
import com.example.crypto_exchange.entity.User;
import com.example.crypto_exchange.entity.UserBalance;
import com.example.crypto_exchange.repository.TokenRepository;
import com.example.crypto_exchange.repository.UserBalanceRepository;
import com.example.crypto_exchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserBalanceServiceTest {

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    private User testUser1;
    private User testUser2;
    private Token ethToken;
    private Token usdcToken;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User("user1@example.com", "Test User 1");
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User("user2@example.com", "Test User 2");
        testUser2 = userRepository.save(testUser2);

        // Create test tokens
        ethToken = new Token("ETH", "Ethereum", 18);
        ethToken = tokenRepository.save(ethToken);

        usdcToken = new Token("USDC", "USD Coin", 6);
        usdcToken = tokenRepository.save(usdcToken);
    }

    @Test
    void testGetBalance_WhenBalanceExists() {
        // Arrange
        BigDecimal initialAmount = new BigDecimal("100.50");
        UserBalance userBalance = new UserBalance(testUser1.getUserId(), ethToken.getTokenId(), initialAmount);
        userBalanceRepository.save(userBalance);

        // Act
        BigDecimal balance = userBalanceService.getBalance(testUser1.getUserId(), ethToken.getTokenId());

        // Assert
        assertEquals(initialAmount, balance);
    }

    @Test
    void testGetBalance_WhenBalanceDoesNotExist() {
        // Act
        BigDecimal balance = userBalanceService.getBalance(testUser1.getUserId(), ethToken.getTokenId());

        // Assert
        assertEquals(BigDecimal.ZERO, balance);
    }

    @Test
    void testGetBalanceBySymbol() {
        // Arrange
        BigDecimal initialAmount = new BigDecimal("500.25");
        UserBalance userBalance = new UserBalance(testUser1.getUserId(), usdcToken.getTokenId(), initialAmount);
        userBalanceRepository.save(userBalance);

        // Act
        BigDecimal balance = userBalanceService.getBalance(testUser1.getUserId(), "USDC");

        // Assert
        assertEquals(initialAmount, balance);
    }

    @Test
    void testSetBalance_NewBalance() {
        // Arrange
        BigDecimal amount = new BigDecimal("250.75");

        // Act
        UserBalance result = userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), amount);

        // Assert
        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertEquals(testUser1.getUserId(), result.getUserId());
        assertEquals(ethToken.getTokenId(), result.getTokenId());

        // Verify in database
        BigDecimal storedBalance = userBalanceService.getBalance(testUser1.getUserId(), ethToken.getTokenId());
        assertEquals(amount, storedBalance);
    }

    @Test
    void testSetBalance_UpdateExistingBalance() {
        // Arrange
        BigDecimal initialAmount = new BigDecimal("100.00");
        BigDecimal updatedAmount = new BigDecimal("200.00");
        
        userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), initialAmount);

        // Act
        UserBalance result = userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), updatedAmount);

        // Assert
        assertEquals(updatedAmount, result.getAmount());

        // Verify in database
        BigDecimal storedBalance = userBalanceService.getBalance(testUser1.getUserId(), ethToken.getTokenId());
        assertEquals(updatedAmount, storedBalance);
    }

    @Test
    void testAddToBalance_NewBalance() {
        // Arrange
        BigDecimal amountToAdd = new BigDecimal("50.25");

        // Act
        UserBalance result = userBalanceService.addToBalance(testUser1.getUserId(), ethToken.getTokenId(), amountToAdd);

        // Assert
        assertEquals(amountToAdd, result.getAmount());

        // Verify in database
        BigDecimal storedBalance = userBalanceService.getBalance(testUser1.getUserId(), ethToken.getTokenId());
        assertEquals(amountToAdd, storedBalance);
    }

    @Test
    void testAddToBalance_ExistingBalance() {
        // Arrange
        BigDecimal initialAmount = new BigDecimal("100.00");
        BigDecimal amountToAdd = new BigDecimal("50.00");
        BigDecimal expectedTotal = new BigDecimal("150.00");

        userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), initialAmount);

        // Act
        UserBalance result = userBalanceService.addToBalance(testUser1.getUserId(), ethToken.getTokenId(), amountToAdd);

        // Assert
        assertEquals(expectedTotal, result.getAmount());

        // Verify in database
        BigDecimal storedBalance = userBalanceService.getBalance(testUser1.getUserId(), ethToken.getTokenId());
        assertEquals(expectedTotal, storedBalance);
    }

    @Test
    void testAddToBalance_InvalidAmount() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userBalanceService.addToBalance(testUser1.getUserId(), ethToken.getTokenId(), BigDecimal.ZERO);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            userBalanceService.addToBalance(testUser1.getUserId(), ethToken.getTokenId(), new BigDecimal("-10"));
        });
    }

    @Test
    void testSubtractFromBalance_SufficientBalance() {
        // Arrange
        BigDecimal initialAmount = new BigDecimal("100.00");
        BigDecimal amountToSubtract = new BigDecimal("30.00");
        BigDecimal expectedRemaining = new BigDecimal("70.00");

        userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), initialAmount);

        // Act
        UserBalance result = userBalanceService.subtractFromBalance(testUser1.getUserId(), ethToken.getTokenId(), amountToSubtract);

        // Assert
        assertEquals(expectedRemaining, result.getAmount());

        // Verify in database
        BigDecimal storedBalance = userBalanceService.getBalance(testUser1.getUserId(), ethToken.getTokenId());
        assertEquals(expectedRemaining, storedBalance);
    }

    @Test
    void testSubtractFromBalance_InsufficientBalance() {
        // Arrange
        BigDecimal initialAmount = new BigDecimal("50.00");
        BigDecimal amountToSubtract = new BigDecimal("100.00");

        userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), initialAmount);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            userBalanceService.subtractFromBalance(testUser1.getUserId(), ethToken.getTokenId(), amountToSubtract);
        });
    }

    @Test
    void testSubtractFromBalance_NoExistingBalance() {
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            userBalanceService.subtractFromBalance(testUser1.getUserId(), ethToken.getTokenId(), new BigDecimal("10.00"));
        });
    }

    @Test
    void testHasSufficientBalance() {
        // Arrange
        BigDecimal balance = new BigDecimal("100.00");
        userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), balance);

        // Act & Assert
        assertTrue(userBalanceService.hasSufficientBalance(testUser1.getUserId(), ethToken.getTokenId(), new BigDecimal("50.00")));
        assertTrue(userBalanceService.hasSufficientBalance(testUser1.getUserId(), ethToken.getTokenId(), new BigDecimal("100.00")));
        assertFalse(userBalanceService.hasSufficientBalance(testUser1.getUserId(), ethToken.getTokenId(), new BigDecimal("150.00")));
    }

    @Test
    void testTransferBalance() {
        // Arrange
        BigDecimal initialBalance1 = new BigDecimal("200.00");
        BigDecimal transferAmount = new BigDecimal("75.00");
        
        userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), initialBalance1);
        userBalanceService.setBalance(testUser2.getUserId(), ethToken.getTokenId(), BigDecimal.ZERO);

        // Act
        userBalanceService.transferBalance(testUser1.getUserId(), testUser2.getUserId(), ethToken.getTokenId(), transferAmount);

        // Assert
        BigDecimal balance1 = userBalanceService.getBalance(testUser1.getUserId(), ethToken.getTokenId());
        BigDecimal balance2 = userBalanceService.getBalance(testUser2.getUserId(), ethToken.getTokenId());

        assertEquals(new BigDecimal("125.00"), balance1);
        assertEquals(transferAmount, balance2);
    }

    @Test
    void testTransferBalance_InsufficientBalance() {
        // Arrange
        BigDecimal initialBalance = new BigDecimal("50.00");
        BigDecimal transferAmount = new BigDecimal("100.00");
        
        userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), initialBalance);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            userBalanceService.transferBalance(testUser1.getUserId(), testUser2.getUserId(), ethToken.getTokenId(), transferAmount);
        });
    }

    @Test
    void testGetAllBalances() {
        // Arrange
        userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), new BigDecimal("100.00"));
        userBalanceService.setBalance(testUser1.getUserId(), usdcToken.getTokenId(), new BigDecimal("500.00"));

        // Act
        List<UserBalance> balances = userBalanceService.getAllBalances(testUser1.getUserId());

        // Assert
        assertEquals(2, balances.size());
    }

    @Test
    void testGetNonZeroBalances() {
        // Arrange
        userBalanceService.setBalance(testUser1.getUserId(), ethToken.getTokenId(), new BigDecimal("100.00"));
        userBalanceService.setBalance(testUser1.getUserId(), usdcToken.getTokenId(), BigDecimal.ZERO);

        // Act
        List<UserBalance> nonZeroBalances = userBalanceService.getNonZeroBalances(testUser1.getUserId());

        // Assert
        assertEquals(1, nonZeroBalances.size());
        assertEquals(ethToken.getTokenId(), nonZeroBalances.get(0).getTokenId());
    }

    @Test
    void testOperationsWithTokenSymbol() {
        // Test set, add, subtract operations using token symbol
        BigDecimal amount = new BigDecimal("100.00");
        
        // Set balance by symbol
        UserBalance result = userBalanceService.setBalance(testUser1.getUserId(), "ETH", amount);
        assertEquals(amount, result.getAmount());
        
        // Add by symbol
        userBalanceService.addToBalance(testUser1.getUserId(), "ETH", new BigDecimal("50.00"));
        BigDecimal newBalance = userBalanceService.getBalance(testUser1.getUserId(), "ETH");
        assertEquals(new BigDecimal("150.00"), newBalance);
        
        // Subtract by symbol
        userBalanceService.subtractFromBalance(testUser1.getUserId(), "ETH", new BigDecimal("25.00"));
        newBalance = userBalanceService.getBalance(testUser1.getUserId(), "ETH");
        assertEquals(new BigDecimal("125.00"), newBalance);
    }

    @Test
    void testValidationErrors() {
        // Test with non-existent user
        assertThrows(IllegalArgumentException.class, () -> {
            userBalanceService.setBalance(999L, ethToken.getTokenId(), new BigDecimal("100.00"));
        });

        // Test with non-existent token
        assertThrows(IllegalArgumentException.class, () -> {
            userBalanceService.setBalance(testUser1.getUserId(), 999L, new BigDecimal("100.00"));
        });

        // Test with non-existent token symbol
        assertThrows(IllegalArgumentException.class, () -> {
            userBalanceService.setBalance(testUser1.getUserId(), "INVALID", new BigDecimal("100.00"));
        });
    }
} 