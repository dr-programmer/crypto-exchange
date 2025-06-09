package com.example.crypto_exchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Web3ServiceTest {

    @Mock
    private Web3j web3j;

    private Web3Service web3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        web3Service = new Web3Service(web3j);
    }

    @Test
    void testGetBlockNumber() throws IOException {
        // Arrange
        EthBlockNumber ethBlockNumber = mock(EthBlockNumber.class);
        when(ethBlockNumber.getBlockNumber()).thenReturn(BigInteger.valueOf(12345));
        org.web3j.protocol.core.Request request = mock(org.web3j.protocol.core.Request.class);
        when(web3j.ethBlockNumber()).thenReturn(request);
        when(request.send()).thenReturn(ethBlockNumber);

        // Act
        BigInteger blockNumber = web3Service.getBlockNumber();

        // Assert
        assertNotNull(blockNumber);
        assertEquals(BigInteger.valueOf(12345), blockNumber);
        verify(web3j).ethBlockNumber();
    }
} 