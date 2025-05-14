package com.example.crypto_exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.crypto_exchange"})
public class CryptoExchangeApplication {
	public static void main(String[] args) {
		SpringApplication.run(CryptoExchangeApplication.class, args);
	}
}
