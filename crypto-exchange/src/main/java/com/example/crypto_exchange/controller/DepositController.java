package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.dto.DepositRequest;
import com.example.crypto_exchange.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/deposit")
public class DepositController {

    @Autowired
    private DepositService depositService;

    @PostMapping
    public ResponseEntity<Map<String, String>> deposit(@RequestBody DepositRequest request) {
        String message = depositService.processDeposit(request);
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
} 