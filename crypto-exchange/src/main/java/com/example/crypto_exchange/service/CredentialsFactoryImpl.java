package com.example.crypto_exchange.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

@Slf4j
@Service
public class CredentialsFactoryImpl implements CredentialsFactory {
    
    @Override
    public Credentials create(String privateKey) {
        log.debug("Creating credentials from private key");
        
        if (privateKey == null || privateKey.trim().isEmpty()) {
            log.error("Private key is null or empty");
            throw new IllegalArgumentException("Private key cannot be null or empty");
        }
        
        try {
            log.trace("Creating Web3j credentials from private key");
            Credentials credentials = Credentials.create(privateKey);
            
            log.info("Credentials created successfully for address: {}", credentials.getAddress());
            log.debug("Private key length: {} characters", privateKey.length());
            
            return credentials;
        } catch (Exception e) {
            log.error("Failed to create credentials from private key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create credentials", e);
        }
    }
} 