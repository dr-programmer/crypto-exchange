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
            Function function = new Function(
                "balanceOf",
                java.util.Collections.singletonList(new Address(walletAddress)),
                java.util.Collections.singletonList(new TypeReference<Uint256>() {})
            );

            String encodedFunction = FunctionEncoder.encode(function);

            org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(walletAddress, tokenAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
            ).send();

            java.util.List<Type> output = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
            BigInteger balance = (output.isEmpty()) ? BigInteger.ZERO : (BigInteger) output.get(0).getValue();

            return new BigDecimal(balance).divide(BigDecimal.TEN.pow(decimals));
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