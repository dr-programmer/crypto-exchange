package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.DepositRequest;
import com.example.crypto_exchange.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock
    private Web3j web3j;

    private DepositService depositService;
    private static final String VALID_ADDRESS = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e";
    private static final String INVALID_ADDRESS = "0xinvalid";

    @BeforeEach
    void setUp() {
        depositService = new DepositService();
    }

    @Test
    void testProcessDeposit_ValidInput() {
        DepositRequest request = new DepositRequest();
        request.setWalletAddress(VALID_ADDRESS);
        request.setToken("ETH");
        request.setAmount(new BigDecimal("1.0"));

        String result = depositService.processDeposit(request);
        assertNotNull(result);
        assertEquals("Deposit simulated successfully", result);
    }

    @Test
    void testDeposit_InvalidWalletAddress() {
        // Arrange
        DepositRequest request = new DepositRequest();
        request.setWalletAddress(INVALID_ADDRESS);
        request.setToken("ETH");
        request.setAmount(new BigDecimal("1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> depositService.processDeposit(request));
    }

    @Test
    void testDeposit_NullWalletAddress() {
        // Arrange
        DepositRequest request = new DepositRequest();
        request.setWalletAddress(null);
        request.setToken("ETH");
        request.setAmount(new BigDecimal("1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> depositService.processDeposit(request));
    }

    @Test
    void testDeposit_UnsupportedToken() {
        // Arrange
        DepositRequest request = new DepositRequest();
        request.setWalletAddress(VALID_ADDRESS);
        request.setToken("UNSUPPORTED");
        request.setAmount(new BigDecimal("1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> depositService.processDeposit(request));
    }

    @Test
    void testDeposit_InvalidAmount() {
        // Arrange
        DepositRequest request = new DepositRequest();
        request.setWalletAddress(VALID_ADDRESS);
        request.setToken("ETH");
        request.setAmount(new BigDecimal("-1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> depositService.processDeposit(request));
    }

    @Test
    void testDeposit_ZeroAmount() {
        // Arrange
        DepositRequest request = new DepositRequest();
        request.setWalletAddress(VALID_ADDRESS);
        request.setToken("ETH");
        request.setAmount(BigDecimal.ZERO);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> depositService.processDeposit(request));
    }

    @Test
    void testDeposit_NullToken() {
        // Arrange
        DepositRequest request = new DepositRequest();
        request.setWalletAddress(VALID_ADDRESS);
        request.setToken(null);
        request.setAmount(new BigDecimal("1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> depositService.processDeposit(request));
    }

    @Test
    void testDeposit_EmptyToken() {
        // Arrange
        DepositRequest request = new DepositRequest();
        request.setWalletAddress(VALID_ADDRESS);
        request.setToken("");
        request.setAmount(new BigDecimal("1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> depositService.processDeposit(request));
    }

    @Test
    void testDeposit_NullAmount() {
        // Arrange
        DepositRequest request = new DepositRequest();
        request.setWalletAddress(VALID_ADDRESS);
        request.setToken("ETH");
        request.setAmount(null);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> depositService.processDeposit(request));
    }
} 