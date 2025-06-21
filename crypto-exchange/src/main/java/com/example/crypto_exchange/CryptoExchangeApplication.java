package com.example.crypto_exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.retry.annotation.EnableRetry;

@Slf4j
@SpringBootApplication
@EnableAsync
@EnableRetry
public class CryptoExchangeApplication {
	
	public static void main(String[] args) {
		log.info("Starting Crypto Exchange Application...");
		log.debug("Application arguments: {}", String.join(" ", args));
		
		try {
			SpringApplication.run(CryptoExchangeApplication.class, args);
			log.info("Crypto Exchange Application started successfully");
		} catch (Exception e) {
			log.error("Failed to start Crypto Exchange Application", e);
			throw e;
		}
	}
}
