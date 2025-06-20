package com.example.crypto_exchange.service;

import com.example.crypto_exchange.entity.Token;
import com.example.crypto_exchange.entity.WalletBalance;
import com.example.crypto_exchange.repository.TokenRepository;
import com.example.crypto_exchange.repository.WalletBalanceRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Periodically polls the blockchain (via {@link BlockchainService}) for each recorded wallet address and
 * credits the internal user balance on new deposits.
 *
 * <p>The <b>WalletBalance</b> table stores the last on-chain balance we know for
 * <code>(walletAddress, tokenContractAddress)</code> along with the <code>userId</code> that owns the
 * address.  Any increase between the current on-chain balance and the stored value is interpreted as a
 * deposit.</p>
 */

@Service
public class DepositWatcherService {

    private static final Logger log = LoggerFactory.getLogger(DepositWatcherService.class);

    private final BlockchainService blockchainService;
    private final WalletBalanceRepository walletBalanceRepository;
    private final UserBalanceService userBalanceService;
    private final TransactionLogService transactionLogService;
    private final TokenRepository tokenRepository;

    @Autowired
    public DepositWatcherService(BlockchainService blockchainService,
                                  WalletBalanceRepository walletBalanceRepository,
                                  UserBalanceService userBalanceService,
                                  TransactionLogService transactionLogService,
                                  TokenRepository tokenRepository) {
        this.blockchainService = blockchainService;
        this.walletBalanceRepository = walletBalanceRepository;
        this.userBalanceService = userBalanceService;
        this.transactionLogService = transactionLogService;
        this.tokenRepository = tokenRepository;
    }


    @Scheduled(fixedDelayString = "${deposit.watcher.fixed-delay-ms:60000}")
    @Transactional
    public void pollBlockchain() {
        Instant start = Instant.now();
        List<WalletBalance> trackedWallets = walletBalanceRepository.findAll();
        log.trace("DepositWatcher polling %d wallet balances".formatted(trackedWallets.size()));

        trackedWallets.forEach(this::checkWallet);

        log.debug("DepositWatcher finished in {} ms", Duration.between(start, Instant.now()).toMillis());
    }


    private void checkWallet(WalletBalance walletBalance) {
        try {
            BigDecimal currentOnChain = fetchOnChainBalance(walletBalance);
            BigDecimal lastKnown = walletBalance.getBalance() == null ? BigDecimal.ZERO : walletBalance.getBalance();

            // Only act on increases (new deposits)
            if (currentOnChain.compareTo(lastKnown) > 0) {
                BigDecimal depositAmount = currentOnChain.subtract(lastKnown);
                handleDeposit(walletBalance, depositAmount);

                walletBalance.setBalance(currentOnChain);
                walletBalanceRepository.save(walletBalance);
            }
        } catch (Exception e) {
            log.error("Error while checking wallet {} / {}: {}", walletBalance.getWalletAddress(), walletBalance.getTokenContractAddress(), e.getMessage(), e);
        }
    }

    private BigDecimal fetchOnChainBalance(WalletBalance wb) {
        String tokenContract = wb.getTokenContractAddress();
        String wallet = wb.getWalletAddress();

        if (tokenContract == null || tokenContract.isBlank()) {
            // Native coin (ETH)
            return blockchainService.getNativeBalance(wallet);
        }
        return blockchainService.getTokenBalance(tokenContract, wallet);
    }

    private void handleDeposit(WalletBalance wb, BigDecimal amount) {
        Long userId = wb.getUserId();
        if (userId == null) {
            log.warn("Wallet {} has no associated user – skipping deposit credit", wb.getWalletAddress());
            return;
        }
        String tokenContract = wb.getTokenContractAddress();

        Optional<Token> tokenOpt;
        if (tokenContract == null || tokenContract.isBlank()) {
            tokenOpt = tokenRepository.findBySymbol("ETH");
        } else {
            tokenOpt = tokenRepository.findByContractAddress(tokenContract);
        }

        if (tokenOpt.isEmpty()) {
            log.warn("Cannot map token for contract '{}' – skipping deposit", tokenContract);
            return;
        }
        String tokenSymbol = tokenOpt.get().getSymbol();

        log.info("Detected deposit → user={} token={} amount={} wallet={}", userId, tokenSymbol, amount, wb.getWalletAddress());

        userBalanceService.addToBalance(userId, tokenSymbol, amount);

        String pseudoTxHash = "SIMULATED_DEPOSIT_" + UUID.randomUUID();
        transactionLogService.logDeposit(userId, tokenSymbol, amount, wb.getWalletAddress(), pseudoTxHash);
    }

    @PostConstruct
    void init() {
        log.info("DepositWatcherService initialised – polling for on-chain deposits");
    }
}