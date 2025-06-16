package com.example.crypto_exchange.integration;

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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
class DatabaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserRepository userRepository;

    private Token testToken;
    private User testUser;
    private UserBalance testBalance;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setCreatedAt(LocalDateTime.now());
        userRepository.save(testUser);

        // Create test token
        testToken = new Token();
        testToken.setSymbol("ETH");
        testToken.setName("Ethereum");
        testToken.setDecimals(18);
        tokenRepository.save(testToken);

        // Create test balance
        testBalance = new UserBalance();
        testBalance.setUserId(testUser.getUserId());
        testBalance.setTokenId(testToken.getTokenId());
        testBalance.setAmount(new BigDecimal("10.0"));
        userBalanceRepository.save(testBalance);
    }

    @Test
    void testTokenCreationAndRetrieval() {
        Optional<Token> retrievedToken = tokenRepository.findBySymbol("ETH");
        assertTrue(retrievedToken.isPresent());
        assertEquals("ETH", retrievedToken.get().getSymbol());
        assertEquals("Ethereum", retrievedToken.get().getName());
    }

    @Test
    void testUserBalanceOperations() {
        // Test balance retrieval
        Optional<UserBalance> retrievedBalance = userBalanceRepository.findByUserIdAndTokenId(testUser.getUserId(), testToken.getTokenId());
        assertTrue(retrievedBalance.isPresent());
        assertEquals(new BigDecimal("10.0"), retrievedBalance.get().getAmount());

        // Test balance update
        BigDecimal newAmount = new BigDecimal("15.0");
        userBalanceRepository.updateBalanceAmount(testUser.getUserId(), testToken.getTokenId(), newAmount);
        userBalanceRepository.flush();
        
        Optional<UserBalance> updatedBalance = userBalanceRepository.findByUserIdAndTokenId(testUser.getUserId(), testToken.getTokenId());
        assertTrue(updatedBalance.isPresent());
        assertTrue(updatedBalance.get().getAmount().compareTo(newAmount) == 0);
    }

    @Test
    void testNonExistentBalance() {
        Optional<UserBalance> nonExistentBalance = userBalanceRepository.findByUserIdAndTokenId(999L, 999L);
        assertFalse(nonExistentBalance.isPresent());
    }
} 