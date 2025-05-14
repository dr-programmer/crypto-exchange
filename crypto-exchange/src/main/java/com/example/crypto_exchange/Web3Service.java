package com.example.crypto_exchange;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

@Service
public class Web3Service {
    private final Web3j web3j;

    public Web3Service(@Value("${infura.url}") String infuraUrl) {
        this.web3j = Web3j.build(new HttpService(infuraUrl));
    }

    public BigInteger getBlockNumber() throws IOException {
        return web3j.ethBlockNumber().send().getBlockNumber();
    }
}
