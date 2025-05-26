package com.example.crypto_exchange.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Service
public class BlockchainService {

    private final Web3j web3j;

    @Autowired
    public BlockchainService(Web3j web3j) {
        this.web3j = web3j;
    }

    /**
     * Get the ETH balance of a wallet address
     * @param address The Ethereum wallet address
     * @return The balance in ETH
     */
    public BigDecimal getEthBalance(String address) {
        try {
            EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                    .send();
            return Convert.fromWei(balance.getBalance().toString(), Unit.ETHER);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get ETH balance for address: " + address, e);
        }
    }

    /**
     * Get the ERC-20 token balance of a wallet address
     * @param tokenAddress The ERC-20 token contract address
     * @param walletAddress The wallet address to check
     * @param decimals The number of decimals the token uses
     * @return The token balance
     */
    public BigDecimal getTokenBalance(String tokenAddress, String walletAddress, int decimals) {
        try {
            // Load the ERC-20 contract
            Contract contract = Contract.load(
                "0x" + tokenAddress,
                web3j,
                null,
                null
            );

            // Call balanceOf function
            BigInteger balance = (BigInteger) contract.call(
                "balanceOf",
                walletAddress
            ).get(0);

            // Convert to decimal with proper decimals
            return new BigDecimal(balance)
                .divide(BigDecimal.valueOf(Math.pow(10, decimals)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get token balance for address: " + walletAddress, e);
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