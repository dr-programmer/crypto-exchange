package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.dto.WithdrawRequest;
import com.example.crypto_exchange.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/withdraw")
public class WithdrawController {

    @Autowired
    private WithdrawService withdrawService;

    @PostMapping
    public ResponseEntity<Map<String, String>> withdraw(@RequestBody WithdrawRequest request) {

        String message = withdrawService.processWithdraw(request);

        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", message);

        return ResponseEntity.ok(response);
    }
}
