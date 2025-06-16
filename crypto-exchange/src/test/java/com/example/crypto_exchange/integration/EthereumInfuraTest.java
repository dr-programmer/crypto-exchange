package com.example.crypto_exchange.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class EthereumInfuraTest {
    private Web3j web3j;
    private static final String TEST_ADDRESS = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"; // Example address

    @BeforeEach
    void setUp() {
        String infuraUrl = "https://mainnet.infura.io/v3/f77ecfe31d0a49078dd1a3dcd0f853b9";
        web3j = Web3j.build(new HttpService(infuraUrl));
    }

    @Test
    void testInfuraConnection() throws Exception {
        // Test network connection
        var netVersion = web3j.netVersion().send();
        assertNotNull(netVersion);
        assertEquals("1", netVersion.getNetVersion()); // Mainnet ID is 1
    }

    @Test
    void testGetBalance() throws Exception {
        // Test balance retrieval
        EthGetBalance ethGetBalance = web3j.ethGetBalance(TEST_ADDRESS, DefaultBlockParameterName.LATEST).send();
        assertNotNull(ethGetBalance);
        assertNotNull(ethGetBalance.getBalance());
        
        BigDecimal balance = Convert.fromWei(new BigDecimal(ethGetBalance.getBalance()), Convert.Unit.ETHER);
        assertTrue(balance.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void testInvalidAddress() throws Exception {
        String invalidAddress = "0x1234567890123456789012345678901234567890";
        EthGetBalance ethGetBalance = web3j.ethGetBalance(invalidAddress, DefaultBlockParameterName.LATEST).send();
        assertNotNull(ethGetBalance);
        assertNotNull(ethGetBalance.getBalance());
    }
} 