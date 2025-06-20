package com.example.crypto_exchange.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableRetry
@EnableScheduling
public class AsyncConfig {
    // Configuration is handled by annotations
} 