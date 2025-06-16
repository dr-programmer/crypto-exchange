package com.example.crypto_exchange.service;

public interface CredentialsFactory {
    org.web3j.crypto.Credentials create(String privateKey);
} 