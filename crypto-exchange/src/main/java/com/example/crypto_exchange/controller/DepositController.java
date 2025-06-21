package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.dto.DepositRequest;
import com.example.crypto_exchange.service.DepositService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/deposit")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> deposit(@Valid @RequestBody DepositRequest request) {
        log.info("Processing deposit request: {}", request);
        
        try {
            String message = depositService.processDeposit(request);
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", message);
            response.put("txId", "DEP_" + System.currentTimeMillis());
            
            log.info("Deposit processed successfully: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Deposit failed", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 