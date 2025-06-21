package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.service.Web3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EthController {

    private final Web3Service web3Service;

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
