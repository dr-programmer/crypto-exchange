package com.example.crypto_exchange.service;

import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.io.IOException;
import java.math.BigInteger;

@Service
public class Web3Service {

    private final Web3j web3j;

    public Web3Service(Web3j web3j) {
        this.web3j = web3j;
    }

    public BigInteger getBlockNumber() throws IOException {
        EthBlockNumber blockNumberResponse = web3j.ethBlockNumber().send();
        return blockNumberResponse.getBlockNumber();
    }
}
