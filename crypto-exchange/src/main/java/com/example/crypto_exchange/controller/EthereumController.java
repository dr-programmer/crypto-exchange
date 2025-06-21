package com.example.crypto_exchange.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/ethereum")
@RequiredArgsConstructor
public class EthereumController {

    private final Web3j web3j;

    @GetMapping("/block-number")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getBlockNumber() {
        log.info("Fetching current Ethereum block number");
        
        try {
            EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
            
            if (blockNumber.hasError()) {
                log.error("Error fetching block number: {}", blockNumber.getError());
                Map<String, Object> response = new HashMap<>();
                response.put("status", "ERROR");
                response.put("message", "Failed to fetch block number: " + blockNumber.getError().getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            BigInteger currentBlock = blockNumber.getBlockNumber();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("blockNumber", currentBlock.toString());
            response.put("blockNumberHex", "0x" + currentBlock.toString(16));
            
            log.info("Current block number: {}", currentBlock);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Exception while fetching block number", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Failed to fetch block number: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 