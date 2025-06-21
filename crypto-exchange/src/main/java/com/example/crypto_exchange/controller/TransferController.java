package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.dto.TransferRequest;
import com.example.crypto_exchange.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> transfer(@Valid @RequestBody TransferRequest request) {
        log.info("Processing transfer request: {}", request);
        
        try {
            String txHash = transferService.processTransfer(request);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("txHash", txHash);
            response.put("txId", "TR_" + System.currentTimeMillis());
            
            log.info("Transfer processed successfully: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Transfer failed", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 