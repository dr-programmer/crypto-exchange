package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.WithdrawRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WithdrawServiceTest {

    private WithdrawService withdrawService;
    private static final String VALID_ADDRESS = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e";

    @BeforeEach
    void setUp() {
        withdrawService = new WithdrawService();
    }

    @Test
    void testProcessWithdraw_ValidInput() {
        WithdrawRequest request = new WithdrawRequest();
        request.setWalletAddress(VALID_ADDRESS);
        request.setToken("ETH");
        request.setAmount(new BigDecimal("1.0"));

        String result = withdrawService.processWithdraw(request);
        assertNotNull(result);
        assertEquals("Withdrawal simulated successfully", result);
    }
} 