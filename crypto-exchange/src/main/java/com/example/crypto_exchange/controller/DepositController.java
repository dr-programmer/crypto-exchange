package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.dto.DepositRequest;
import com.example.crypto_exchange.service.DepositService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/deposit")
public class DepositController {

    @Autowired
    private DepositService depositService;

    @PostMapping
    public ResponseEntity<Map<String, String>> deposit(@Valid @RequestBody DepositRequest request) {
        String txId = depositService.processDeposit(request);
        
        Map<String, String> response = new HashMap<>();
        response.put("txId", txId);
        response.put("status", "success");
        response.put("message", "Deposit processed successfully");
        
        return ResponseEntity.ok(response);
    }
} 