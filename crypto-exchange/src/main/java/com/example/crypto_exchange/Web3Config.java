package com.example.crypto_exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Slf4j
@Configuration
public class Web3Config {

    @Value("${infura.api.url}")
    private String infuraUrl;

    @Bean
    public Web3j web3j() {
        log.info("Initializing Web3j with Infura URL: {}", infuraUrl);
        
        try {
            HttpService httpService = new HttpService(infuraUrl);
            Web3j web3j = Web3j.build(httpService);
            
            log.info("Web3j initialized successfully");
            log.debug("Web3j instance created with HTTP service for URL: {}", infuraUrl);
            
            return web3j;
        } catch (Exception e) {
            log.error("Failed to initialize Web3j with URL: {}", infuraUrl, e);
            throw new RuntimeException("Failed to initialize Web3j", e);
        }
    }
}
