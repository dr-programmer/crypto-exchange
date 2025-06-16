package com.example.crypto_exchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockchainServiceTest {

    private BlockchainService blockchainService;
    private Web3j web3j;
    private static final String TEST_ADDRESS = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e";

    @BeforeEach
    void setUp() {
        web3j = Mockito.mock(Web3j.class);
        blockchainService = new BlockchainService(web3j);
    }

    @Test
    void getEthBalance_Success() throws Exception {
        // Arrange
        BigInteger balanceWei = Convert.toWei(BigDecimal.valueOf(1.5), Convert.Unit.ETHER).toBigInteger();
        EthGetBalance ethGetBalance = new EthGetBalance();
        ethGetBalance.setResult(Numeric.toHexStringWithPrefixSafe(balanceWei));

        var requestMock = mock(org.web3j.protocol.core.Request.class);
        when(web3j.ethGetBalance(TEST_ADDRESS, DefaultBlockParameterName.LATEST)).thenReturn(requestMock);
        when(requestMock.send()).thenReturn(ethGetBalance);

        // Act
        BigDecimal balance = blockchainService.getEthBalance(TEST_ADDRESS);

        // Assert
        assertNotNull(balance);
        assertEquals(new BigDecimal("1.5"), balance);
    }

    @Test
    void getEthBalance_InvalidAddress() {
        // Arrange
        String invalidAddress = "0xinvalid";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            blockchainService.getEthBalance(invalidAddress);
        });
    }

    @Test
    void getTokenBalance_NotImplemented() {
        // Arrange
        String tokenAddress = "0x1234567890123456789012345678901234567890";
        int decimals = 18;

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            blockchainService.getTokenBalance(tokenAddress, TEST_ADDRESS, decimals);
        });
    }

    @Test
    void prepareTransaction_NotImplemented() {
        // Arrange
        String fromAddress = "0x1234567890123456789012345678901234567890";
        String toAddress = "0x0987654321098765432109876543210987654321";
        BigDecimal amount = BigDecimal.ONE;

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            blockchainService.prepareTransaction(fromAddress, toAddress, amount);
        });
    }

    @Test
    void getTokenBalance_Success() throws Exception {
        String tokenAddress = "0x1234567890123456789012345678901234567890";
        int decimals = 18;
        BigInteger rawBalance = new BigInteger("1000000000000000000"); // 1 token with 18 decimals
        String encodedResult = "0x" + String.format("%064x", rawBalance);

        EthCall ethCall = new EthCall();
        ethCall.setResult(encodedResult);

        var requestMock = mock(org.web3j.protocol.core.Request.class);
        when(web3j.ethCall(any(Transaction.class), eq(DefaultBlockParameterName.LATEST))).thenReturn(requestMock);
        when(requestMock.send()).thenReturn(ethCall);

        BigDecimal balance = blockchainService.getTokenBalance(tokenAddress, TEST_ADDRESS, decimals);
        assertNotNull(balance);
        assertEquals(BigDecimal.ONE, balance);
    }

    @Test
    void getTokenBalance_Failure() throws Exception {
        String tokenAddress = "0x1234567890123456789012345678901234567890";
        int decimals = 18;

        var requestMock = mock(org.web3j.protocol.core.Request.class);
        when(web3j.ethCall(any(Transaction.class), eq(DefaultBlockParameterName.LATEST))).thenReturn(requestMock);
        when(requestMock.send()).thenThrow(new RuntimeException("eth_call failed"));

        assertThrows(RuntimeException.class, () -> {
            blockchainService.getTokenBalance(tokenAddress, TEST_ADDRESS, decimals);
        });
    }
} 