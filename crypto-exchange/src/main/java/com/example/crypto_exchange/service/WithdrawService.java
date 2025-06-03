package com.example.crypto_exchange.service;

import com.example.crypto_exchange.dto.WithdrawRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WithdrawService {

    private static final Logger log = LoggerFactory.getLogger(WithdrawService.class);
    
    private final TokenRepository tokenRepository;
    private final UserBalanceRepository userBalanceRepository;

    @Autowired
    public WithdrawService(TokenRepository tokenRepository, UserBalanceRepository userBalanceRepository) {
        this.tokenRepository = tokenRepository;
        this.userBalanceRepository = userBalanceRepository;
    }

     /**
     * Process a withdrawal request
     * @param request The withdrawal request containing user, token, and amount details
     * @return A status message indicating the result of the withdrawal
     * @throws TokenNotFoundException if the requested token doesn't exist
     * @throws InsufficientBalanceException if the user has insufficient balance
     * @throws IllegalArgumentException if the withdrawal amount is invalid
     */
    public String processWithdraw(WithdrawRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
       Token token = tokenRepository.findBySymbol(request.getTokenSymbol())
            .orElseThrow(() -> new TokenNotFoundException(request.getTokenSymbol()));

        UserBalance userBalance = userBalanceRepository.findByUserIdAndTokenId(request.getUserId(), token.getTokenId())
            .orElseThrow(() -> new UserBalanceNotFoundException(request.getUserId(), token.getTokenId()));

        if (!userBalance.hasSufficientBalance(request.getAmount())) {
            throw new InsufficientBalanceException(userBalance.getAmount(), request.getAmount());
        }

        BigDecimal newAmount = userBalance.getAmount().subtract(request.getAmount());
        userBalanceRepository.updateBalanceAmount(request.getUserId(), token.getTokenId(), newAmount);

        return "Withdrawal processed successfully";
    }
}
