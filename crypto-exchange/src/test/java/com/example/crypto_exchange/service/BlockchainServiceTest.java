package com.example.crypto_exchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BlockchainServiceTest {

    private BlockchainService blockchainService;
    private Web3j web3j;
    private static final String TEST_ADDRESS = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e";

    @BeforeEach
    void setUp() throws Exception {
        web3j = Mockito.mock(Web3j.class);
        blockchainService = new BlockchainService();
        // Inject mock Web3j via reflection
        Field web3jField = BlockchainService.class.getDeclaredField("web3j");
        web3jField.setAccessible(true);
        web3jField.set(blockchainService, web3j);
    }

    @Test
    void getNativeBalance_Success() throws Exception {
        // Arrange
        BigInteger balanceWei = Convert.toWei(BigDecimal.valueOf(1.5), Convert.Unit.ETHER).toBigInteger();
        EthGetBalance ethGetBalance = new EthGetBalance();
        ethGetBalance.setResult(Numeric.toHexStringWithPrefixSafe(balanceWei));

        @SuppressWarnings("unchecked")
        Request<?, EthGetBalance> requestMock = (Request<?, EthGetBalance>) mock(Request.class);
        when(web3j.ethGetBalance(TEST_ADDRESS, DefaultBlockParameterName.LATEST)).thenReturn((Request) requestMock);
        when(requestMock.send()).thenReturn(ethGetBalance);

        // Act
        BigDecimal balance = blockchainService.getNativeBalance(TEST_ADDRESS);

        // Assert
        assertNotNull(balance);
        assertEquals(new BigDecimal("1.5"), balance);
    }

    @Test
    void getNativeBalance_InvalidAddress() {
        // Arrange
        String invalidAddress = "0xinvalid";
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            blockchainService.getNativeBalance(invalidAddress);
        });
    }

    @Test
    void getTokenBalance_Placeholder() {
        // Arrange
        String tokenAddress = "0x1234567890123456789012345678901234567890";
        // Act
        BigDecimal balance = blockchainService.getTokenBalance(tokenAddress, TEST_ADDRESS);
        // Assert
        assertNotNull(balance);
        assertEquals(BigDecimal.ZERO, balance);
    }

    @Test
    void prepareTransaction_NotImplemented() {
        // Arrange
        String fromAddress = "0x1234567890123456789012345678901234567890";
        String toAddress = "0x0987654321098765432109876543210987654321";
        BigDecimal amount = BigDecimal.ONE;
        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            blockchainService.prepareTransaction(fromAddress, toAddress, amount);
        });
    }
} 