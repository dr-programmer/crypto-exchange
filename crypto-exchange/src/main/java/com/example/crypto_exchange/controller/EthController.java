package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.service.Web3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

@RestController
public class EthController {

    private static final Logger log = LoggerFactory.getLogger(EthController.class);

    @Autowired
    private Web3Service web3Service;

    @GetMapping("/block")
    public ResponseEntity<BigInteger> getBlockNumber() {
        try {
            BigInteger blockNumber = web3Service.getBlockNumber();
            return ResponseEntity.ok(blockNumber);
        } catch (Exception e) {
            log.error("Error fetching block number", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
