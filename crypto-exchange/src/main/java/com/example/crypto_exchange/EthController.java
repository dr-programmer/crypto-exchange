package com.example.crypto_exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.math.BigInteger;

@RestController
public class EthController {
    @Autowired Web3Service web3Service;

    @GetMapping("/block")
    public BigInteger getBlock() throws IOException {
        return web3Service.getBlockNumber();
    }

}
