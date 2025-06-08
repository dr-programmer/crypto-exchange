package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.dto.WithdrawRequest;
import com.example.crypto_exchange.exception.WithdrawException;
import com.example.crypto_exchange.service.WithdrawService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/withdraw")
public class WithdrawController {
    
    private static final Logger log = LoggerFactory.getLogger(WithdrawController.class);

    private final WithdrawService withdrawService;
    private final Bucket rateLimitBucket;

    @Autowired
    public WithdrawController(WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
        
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        this.rateLimitBucket = Bucket4j.builder().addLimit(limit).build();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CompletableFuture<ResponseEntity<?>> withdraw(@Valid @RequestBody WithdrawRequest request) {
        if (!rateLimitBucket.tryConsume(1)) {
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(createErrorResponse("TOO_MANY_REQUESTS", "Rate limit exceeded. Please try again later."))
            );
        }

        return withdrawService.processWithdraw(request)
            .thenApply(response -> ResponseEntity.ok(response))
            .exceptionally(throwable -> {
                Throwable cause = throwable.getCause();
                if (cause instanceof WithdrawException) {
                    WithdrawException we = (WithdrawException) cause;
                    log.warn("Withdrawal failed: {} ({})", we.getMessage(), we.getErrorCode());
                    return ResponseEntity.badRequest()
                        .body(createErrorResponse(we.getErrorCode(), we.getMessage()));
                } else {
                    log.error("Unexpected error during withdrawal", throwable);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
                }
            });
    }

    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}
    