package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.WithdrawRequest;
import com.example.crypto_exchange.dto.WithdrawResponse;
import com.example.crypto_exchange.entity.Token;
import com.example.crypto_exchange.entity.UserBalance;
import com.example.crypto_exchange.exception.WithdrawException;
import com.example.crypto_exchange.repository.TokenRepository;
import com.example.crypto_exchange.repository.UserBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.tx.Transfer;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.RemoteCall;
import org.junit.jupiter.api.AfterEach;
import org.springframework.test.util.ReflectionTestUtils;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WithdrawServiceTest {

    private WithdrawService withdrawService;
    private TokenRepository tokenRepository;
    private UserBalanceRepository userBalanceRepository;
    private Web3j web3j;
    private CredentialsFactory credentialsFactory;
    private static final String VALID_ADDRESS = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e";
    private static final Long USER_ID = 1L;
    private static final String TOKEN_SYMBOL = "ETH";
    private MockedStatic<Transfer> mockedTransfer;
    private static final String TEST_PRIVATE_KEY = "0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef";

    @BeforeEach
    void setUp() throws Exception {
        tokenRepository = Mockito.mock(TokenRepository.class);
        userBalanceRepository = Mockito.mock(UserBalanceRepository.class);
        web3j = Mockito.mock(Web3j.class);
        credentialsFactory = Mockito.mock(CredentialsFactory.class);
        
        // Create a test instance of WithdrawService with the correct constructor
        withdrawService = new WithdrawService(tokenRepository, userBalanceRepository, web3j, credentialsFactory);
        // Use the dummy private key that triggers mock behavior in WithdrawService
        ReflectionTestUtils.setField(withdrawService, "exchangeWalletPrivateKey", "dummy_private_key");
        
        // Create a proper ECKeyPair for the credentials
        ECKeyPair keyPair = Keys.createEcKeyPair();
        Credentials credentials = Credentials.create(keyPair);
        when(credentialsFactory.create(anyString())).thenReturn(credentials);
        
        // Mock Web3j methods that are called during blockchain transactions
        mockWeb3jMethods();
        
        // Mock Transfer static methods to avoid actual blockchain calls
        mockedTransfer = mockStatic(Transfer.class);
        TransactionReceipt receipt = mock(TransactionReceipt.class);
        when(receipt.getTransactionHash()).thenReturn("0x123...");
        when(receipt.isStatusOK()).thenReturn(true);
        RemoteCall<TransactionReceipt> remoteCall = mock(RemoteCall.class);
        when(remoteCall.send()).thenReturn(receipt);
        
        // Mock the Transfer.sendFunds method to return our mocked remote call
        mockedTransfer.when(() -> Transfer.sendFunds(any(Web3j.class), any(Credentials.class), anyString(), any(BigDecimal.class), any(Convert.Unit.class))).thenReturn(remoteCall);
    }

    private void mockWeb3jMethods() throws Exception {
        // Mock ethGasPrice
        @SuppressWarnings("unchecked")
        Request<?, EthGasPrice> gasPriceRequest = (Request<?, EthGasPrice>) mock(Request.class);
        EthGasPrice gasPriceResponse = new EthGasPrice();
        gasPriceResponse.setResult("0x3b9aca00"); // 1 Gwei
        when(web3j.ethGasPrice()).thenReturn((Request) gasPriceRequest);
        when(gasPriceRequest.send()).thenReturn(gasPriceResponse);
        
        // Mock ethGetTransactionCount
        @SuppressWarnings("unchecked")
        Request<?, EthGetTransactionCount> nonceRequest = (Request<?, EthGetTransactionCount>) mock(Request.class);
        EthGetTransactionCount nonceResponse = new EthGetTransactionCount();
        nonceResponse.setResult("0x0");
        when(web3j.ethGetTransactionCount(anyString(), any(DefaultBlockParameterName.class))).thenReturn((Request) nonceRequest);
        when(nonceRequest.send()).thenReturn(nonceResponse);
        
        // Mock ethGetBalance
        @SuppressWarnings("unchecked")
        Request<?, EthGetBalance> balanceRequest = (Request<?, EthGetBalance>) mock(Request.class);
        EthGetBalance balanceResponse = new EthGetBalance();
        BigInteger balanceWei = Convert.toWei(BigDecimal.valueOf(10.0), Convert.Unit.ETHER).toBigInteger();
        balanceResponse.setResult(Numeric.toHexStringWithPrefixSafe(balanceWei));
        when(web3j.ethGetBalance(anyString(), any(DefaultBlockParameterName.class))).thenReturn((Request) balanceRequest);
        when(balanceRequest.send()).thenReturn(balanceResponse);
        
        // Mock ethSendTransaction
        @SuppressWarnings("unchecked")
        Request<?, EthSendTransaction> sendRequest = (Request<?, EthSendTransaction>) mock(Request.class);
        EthSendTransaction sendResponse = new EthSendTransaction();
        sendResponse.setResult("0x123..."); // Match TransactionReceipt hash
        when(web3j.ethSendTransaction(any(org.web3j.protocol.core.methods.request.Transaction.class))).thenReturn((Request) sendRequest);
        when(sendRequest.send()).thenReturn(sendResponse);
        
        // Mock ethSendRawTransaction
        @SuppressWarnings("unchecked")
        Request<?, EthSendTransaction> rawSendRequest = (Request<?, EthSendTransaction>) mock(Request.class);
        EthSendTransaction rawSendResponse = new EthSendTransaction();
        rawSendResponse.setResult("0x123..."); // Match TransactionReceipt hash
        when(web3j.ethSendRawTransaction(anyString())).thenReturn((Request) rawSendRequest);
        when(rawSendRequest.send()).thenReturn(rawSendResponse);
    }

    @AfterEach
    void tearDown() {
        if (mockedTransfer != null) {
            mockedTransfer.close();
        }
    }

    @Test
    void processWithdraw_ValidInput() throws Exception {
        // Arrange
        WithdrawRequest request = createValidWithdrawRequest();
        Token token = createTestToken();
        UserBalance userBalance = createTestUserBalance();
        
        when(tokenRepository.findBySymbol(TOKEN_SYMBOL)).thenReturn(Optional.of(token));
        when(userBalanceRepository.findByUserIdAndTokenId(USER_ID, token.getTokenId())).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.updateBalanceAmount(any(), any(), any())).thenReturn(1);

        // Act
        CompletableFuture<WithdrawResponse> future = withdrawService.processWithdraw(request);
        WithdrawResponse response = future.get();

        // Assert
        assertNotNull(response);
        assertTrue(response.getTxHash().startsWith("0x") && response.getTxHash().contains("mock_tx_hash_for_testing"));
        assertEquals("SUCCESS", response.getStatus());
        
        // Verify interactions
        verify(tokenRepository).findBySymbol(TOKEN_SYMBOL);
        verify(userBalanceRepository).findByUserIdAndTokenId(USER_ID, token.getTokenId());
        verify(userBalanceRepository).updateBalanceAmount(any(), any(), any());
    }

    @Test
    void processWithdraw_InvalidAddress() {
        // Arrange
        WithdrawRequest request = createValidWithdrawRequest();
        request.setToAddress("invalid_address");

        // Act & Assert
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            withdrawService.processWithdraw(request).get();
        });
        
        assertTrue(exception.getCause() instanceof WithdrawException);
        WithdrawException ex = (WithdrawException) exception.getCause();
        assertEquals("INVALID_ADDRESS", ex.getErrorCode());
        assertEquals("Invalid Ethereum address format", ex.getMessage());
    }

    @Test
    void processWithdraw_TokenNotFound() {
        // Arrange
        WithdrawRequest request = createValidWithdrawRequest();
        when(tokenRepository.findBySymbol(TOKEN_SYMBOL)).thenReturn(Optional.empty());

        // Act & Assert
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            withdrawService.processWithdraw(request).get();
        });
        assertTrue(exception.getCause() instanceof WithdrawException);
        assertEquals("TOKEN_NOT_FOUND", ((WithdrawException) exception.getCause()).getErrorCode());
    }

    @Test
    void processWithdraw_InsufficientBalance() {
        // Arrange
        WithdrawRequest request = createValidWithdrawRequest();
        request.setAmount(new BigDecimal("1000.0")); // Large amount
        Token token = createTestToken();
        UserBalance userBalance = createTestUserBalance();
        
        when(tokenRepository.findBySymbol(TOKEN_SYMBOL)).thenReturn(Optional.of(token));
        when(userBalanceRepository.findByUserIdAndTokenId(USER_ID, token.getTokenId()))
            .thenReturn(Optional.of(userBalance));

        // Act & Assert
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            withdrawService.processWithdraw(request).get();
        });
        assertTrue(exception.getCause() instanceof WithdrawException);
        assertEquals("INSUFFICIENT_BALANCE", ((WithdrawException) exception.getCause()).getErrorCode());
    }

    private WithdrawRequest createValidWithdrawRequest() {
        WithdrawRequest request = new WithdrawRequest();
        request.setUserId(USER_ID);
        request.setTokenSymbol(TOKEN_SYMBOL);
        request.setAmount(new BigDecimal("1.0"));
        request.setToAddress(VALID_ADDRESS);
        return request;
    }

    private Token createTestToken() {
        Token token = new Token();
        token.setTokenId(1L);
        token.setSymbol(TOKEN_SYMBOL);
        token.setName("Ethereum");
        token.setDecimals(18);
        return token;
    }

    private UserBalance createTestUserBalance() {
        UserBalance balance = new UserBalance();
        balance.setUserId(USER_ID);
        balance.setTokenId(1L);
        balance.setAmount(new BigDecimal("10.0"));
        return balance;
    }
} 