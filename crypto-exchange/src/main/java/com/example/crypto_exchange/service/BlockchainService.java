package com.example.crypto_exchange.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.request.Transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Service
public class BlockchainService {
    private static final Logger log = LoggerFactory.getLogger(BlockchainService.class);

    @Autowired
    private Web3j web3j;

    /**
     * Get the ETH balance of a wallet address
     * @param address The Ethereum wallet address
     * @return The balance in ETH
     */
    public BigDecimal getNativeBalance(String address) {
        try {
            EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            return Convert.fromWei(balance.getBalance().toString(), Convert.Unit.ETHER);
        } catch (Exception e) {
            log.error("Error getting native balance for address {}: {}", address, e.getMessage());
            throw new RuntimeException("Failed to get native balance", e);
        }
    }

    /**
     * Get the ERC-20 token balance of a wallet address
     * @param tokenContractAddress The ERC-20 token contract address
     * @param walletAddress The wallet address to check
     * @return The token balance
     */
    public BigDecimal getTokenBalance(String tokenContractAddress, String walletAddress) {
        try {
            ContractGasProvider gasProvider = new DefaultGasProvider();
            ReadonlyTransactionManager txManager = new ReadonlyTransactionManager(web3j, walletAddress);
            
            ERC20 token = ERC20.load(tokenContractAddress, web3j, txManager, gasProvider);
            BigInteger balance = token.balanceOf(walletAddress).send();
            
            // Get token decimals
            BigInteger decimals = token.decimals().send();
            BigDecimal divisor = BigDecimal.valueOf(10).pow(decimals.intValue());
            
            return new BigDecimal(balance).divide(divisor);
        } catch (Exception e) {
            log.error("Error getting token balance for address {} and token {}: {}", 
                     walletAddress, tokenContractAddress, e.getMessage());
            throw new RuntimeException("Failed to get token balance", e);
        }
    }

    /**
     * Prepare a transaction (stub for future implementation)
     * @param fromAddress The sender's address
     * @param toAddress The recipient's address
     * @param amount The amount to send
     * @return A CompletableFuture that will complete with the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> prepareTransaction(
            String fromAddress,
            String toAddress,
            BigDecimal amount) {
        // TODO: Implement transaction preparation and sending
        throw new UnsupportedOperationException("Transaction sending not yet implemented");
    }
} 