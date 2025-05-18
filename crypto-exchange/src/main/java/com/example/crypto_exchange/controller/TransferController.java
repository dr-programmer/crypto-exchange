package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.dto.TransferRequest;
import com.example.crypto_exchange.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping
    public ResponseEntity<Map<String, String>> transfer(@RequestBody TransferRequest request) {
        String txHash = transferService.processTransfer(request);
        
        Map<String, String> response = new HashMap<>();
        response.put("txHash", txHash);
        
        return ResponseEntity.ok(response);
    }
} 