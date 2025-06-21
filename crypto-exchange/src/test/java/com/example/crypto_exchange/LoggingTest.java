package com.example.crypto_exchange;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class LoggingTest {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingTest.class);
    
    @Test
    public void testLoggingLevels() {
        log.trace("This is a TRACE message");
        log.debug("This is a DEBUG message");
        log.info("This is an INFO message");
        log.warn("This is a WARN message");
        log.error("This is an ERROR message");
        
        // Test logging with parameters
        String userId = "123";
        String tokenSymbol = "ETH";
        double amount = 1.5;
        
        log.info("Processing transaction - User: {}, Token: {}, Amount: {}", userId, tokenSymbol, amount);
        log.debug("Transaction details - User ID: {}, Token Symbol: {}, Amount: {}", userId, tokenSymbol, amount);
        
        // Test exception logging
        try {
            throw new RuntimeException("Test exception for logging");
        } catch (Exception e) {
            log.error("Caught exception during test", e);
        }
    }
    
    @Test
    public void testPerformanceLogging() {
        long startTime = System.currentTimeMillis();
        
        // Simulate some work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("Test operation completed in {}ms", duration);
    }
} 