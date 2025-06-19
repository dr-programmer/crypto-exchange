package com.example.crypto_exchange.service;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

@Service
public class CredentialsFactoryImpl implements CredentialsFactory {
    
    @Override
    public Credentials create(String privateKey) {
        if (privateKey == null || privateKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Private key cannot be null or empty");
        }
        return Credentials.create(privateKey);
    }
} 