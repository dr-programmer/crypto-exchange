package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.TransferRequest;
import com.example.crypto_exchange.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private Web3j web3j;

    private TransferService transferService;
    private static final String VALID_PRIVATE_KEY = "0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef";
    private static final String INVALID_PRIVATE_KEY = "0xinvalid";
    private static final String VALID_ADDRESS = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e";
    private static final String INVALID_ADDRESS = "0xinvalid";

    @BeforeEach
    void setUp() {
        transferService = new TransferService();
    }

    @Test
    void testProcessTransfer_ValidInput() {
        TransferRequest request = new TransferRequest();
        request.setPrivateKey(VALID_PRIVATE_KEY);
        request.setRecipientAddress(VALID_ADDRESS);
        request.setAmount(new BigDecimal("1.0"));

        String result = transferService.processTransfer(request);
        assertNotNull(result);
        assertEquals("0x123abc", result);
    }

    @Test
    void testTransfer_InvalidPrivateKey() {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setPrivateKey(INVALID_PRIVATE_KEY);
        request.setRecipientAddress(VALID_ADDRESS);
        request.setAmount(new BigDecimal("1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> transferService.processTransfer(request));
    }

    @Test
    void testTransfer_NullPrivateKey() {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setPrivateKey(null);
        request.setRecipientAddress(VALID_ADDRESS);
        request.setAmount(new BigDecimal("1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> transferService.processTransfer(request));
    }

    @Test
    void testTransfer_InvalidRecipientAddress() {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setPrivateKey(VALID_PRIVATE_KEY);
        request.setRecipientAddress(INVALID_ADDRESS);
        request.setAmount(new BigDecimal("1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> transferService.processTransfer(request));
    }

    @Test
    void testTransfer_NullRecipientAddress() {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setPrivateKey(VALID_PRIVATE_KEY);
        request.setRecipientAddress(null);
        request.setAmount(new BigDecimal("1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> transferService.processTransfer(request));
    }

    @Test
    void testTransfer_InvalidAmount() {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setPrivateKey(VALID_PRIVATE_KEY);
        request.setRecipientAddress(VALID_ADDRESS);
        request.setAmount(new BigDecimal("-1.0"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> transferService.processTransfer(request));
    }

    @Test
    void testTransfer_ZeroAmount() {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setPrivateKey(VALID_PRIVATE_KEY);
        request.setRecipientAddress(VALID_ADDRESS);
        request.setAmount(BigDecimal.ZERO);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> transferService.processTransfer(request));
    }

    @Test
    void testTransfer_NullAmount() {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setPrivateKey(VALID_PRIVATE_KEY);
        request.setRecipientAddress(VALID_ADDRESS);
        request.setAmount(null);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> transferService.processTransfer(request));
    }

    @Test
    void testTransfer_TransactionFailure() throws Exception {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setPrivateKey(VALID_PRIVATE_KEY);
        request.setRecipientAddress(VALID_ADDRESS);
        request.setAmount(new BigDecimal("1.0"));

        when(web3j.ethSendTransaction(any())).thenThrow(new RuntimeException("Transaction failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> transferService.processTransfer(request));
    }
} 