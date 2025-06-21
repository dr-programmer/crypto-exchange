package com.example.crypto_exchange.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class StartupLogger {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${spring.datasource.url:unknown}")
    private String databaseUrl;
    
    @Value("${infura.api.url:unknown}")
    private String infuraUrl;
    
    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationStartup() {
        log.info("=".repeat(80));
        log.info("CRYPTO EXCHANGE APPLICATION STARTED SUCCESSFULLY");
        log.info("=".repeat(80));
        log.info("Startup Time: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        log.info("Server Port: {}", serverPort);
        log.info("Database URL: {}", databaseUrl);
        log.info("Infura URL: {}", infuraUrl);
        
        // Log JVM information
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        log.info("JVM Version: {}", runtimeBean.getVmVersion());
        log.info("JVM Vendor: {}", runtimeBean.getVmVendor());
        log.info("JVM Name: {}", runtimeBean.getVmName());
        log.info("JVM Uptime: {} ms", runtimeBean.getUptime());
        
        // Log memory information
        long maxHeapMemory = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);
        long usedHeapMemory = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long maxNonHeapMemory = memoryBean.getNonHeapMemoryUsage().getMax() / (1024 * 1024);
        long usedNonHeapMemory = memoryBean.getNonHeapMemoryUsage().getUsed() / (1024 * 1024);
        
        log.info("Max Heap Memory: {} MB", maxHeapMemory);
        log.info("Used Heap Memory: {} MB", usedHeapMemory);
        log.info("Max Non-Heap Memory: {} MB", maxNonHeapMemory);
        log.info("Used Non-Heap Memory: {} MB", usedNonHeapMemory);
        
        log.info("=".repeat(80));
        log.info("Application is ready to handle requests");
        log.info("=".repeat(80));
    }
} 