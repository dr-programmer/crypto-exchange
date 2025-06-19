package com.example.crypto_exchange.repository;

import com.example.crypto_exchange.entity.WalletBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletBalanceRepository extends JpaRepository<WalletBalance, Long> {
    Optional<WalletBalance> findByWalletAddressAndTokenContractAddress(
        String walletAddress,
        String tokenContractAddress
    );
} 