package com.example.crypto_exchange.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@Configuration
@EnableAsync
@EnableRetry
@EnableScheduling
public class AsyncConfig {
    
    public AsyncConfig() {
        log.info("AsyncConfig initialized - enabling async processing, retry mechanism, and scheduling");
        log.debug("Configuration includes: @EnableAsync, @EnableRetry, @EnableScheduling");
    }
} 