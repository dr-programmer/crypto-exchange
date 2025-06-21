package com.example.crypto_exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class Web3Service {

    private final Web3j web3j;

    public BigInteger getBlockNumber() throws IOException {
        log.debug("Fetching current Ethereum block number");
        
        try {
            EthBlockNumber blockNumberResponse = web3j.ethBlockNumber().send();
            
            if (blockNumberResponse.hasError()) {
                log.error("Error fetching block number: {}", blockNumberResponse.getError());
                throw new IOException("Failed to fetch block number: " + blockNumberResponse.getError().getMessage());
            }
            
            BigInteger blockNumber = blockNumberResponse.getBlockNumber();
            log.info("Current Ethereum block number: {}", blockNumber);
            log.debug("Block number response received successfully");
            
            return blockNumber;
        } catch (IOException e) {
            log.error("IOException while fetching block number", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching block number", e);
            throw new IOException("Unexpected error while fetching block number", e);
        }
    }
}
