package com.example.crypto_exchange.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(Authentication authentication) {
        log.info("Authentication attempt for user: {}", authentication.getName());
        log.debug("User authorities: {}", authentication.getAuthorities());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        
        log.info("Login successful for user: {}", authentication.getName());
        return ResponseEntity.ok(response);
    }
} 