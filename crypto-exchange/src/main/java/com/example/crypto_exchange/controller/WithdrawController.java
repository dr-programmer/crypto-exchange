package com.example.crypto_exchange.controller;

import com.example.crypto_exchange.dto.WithdrawRequest;
import com.example.crypto_exchange.dto.WithdrawResponse;
import com.example.crypto_exchange.exception.WithdrawException;
import com.example.crypto_exchange.service.WithdrawService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public CompletableFuture<ResponseEntity<WithdrawResponse>> withdraw(@Valid @RequestBody WithdrawRequest request) {
        log.info("Processing async withdrawal request: {}", request);
        
        if (!rateLimitBucket.tryConsume(1)) {
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new WithdrawResponse(null, null, "ERROR: TOO_MANY_REQUESTS: Rate limit exceeded. Please try again later."))
            );
        }

        return withdrawService.processWithdraw(request)
            .thenApply(response -> {
                log.info("Async withdrawal successful: {}", response);
                return ResponseEntity.ok(response);
            })
            .exceptionally(throwable -> {
                Throwable cause = throwable.getCause();
                if (cause instanceof WithdrawException) {
                    WithdrawException we = (WithdrawException) cause;
                    log.warn("Async withdrawal failed: {} ({})", we.getMessage(), we.getErrorCode());
                    return ResponseEntity.badRequest().body(new WithdrawResponse(null, null, "ERROR: " + we.getErrorCode() + ": " + we.getMessage()));
                } else {
                    log.error("Unexpected error during async withdrawal", throwable);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new WithdrawResponse(null, null, "ERROR: INTERNAL_ERROR: An unexpected error occurred"));
                }
            });
    }

    @PostMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WithdrawResponse> withdrawTest(@Valid @RequestBody WithdrawRequest request) {
        log.info("Testing withdraw with request: {}", request);
        
        try {
            // Simple synchronous test
            WithdrawResponse response = new WithdrawResponse(
                "TEST_" + System.currentTimeMillis(),
                "0x" + System.currentTimeMillis() + "test_hash",
                "SUCCESS"
            );
            
            log.info("Test withdraw successful: {}", response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Test withdraw failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new WithdrawResponse(null, null, "ERROR: " + e.getMessage()));
        }
    }

    @PostMapping("/sync")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WithdrawResponse> withdrawSync(@Valid @RequestBody WithdrawRequest request) {
        log.info("Processing synchronous withdrawal request: {}", request);
        
        if (!rateLimitBucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new WithdrawResponse(null, null, "ERROR: TOO_MANY_REQUESTS: Rate limit exceeded. Please try again later."));
        }

        try {
            WithdrawResponse response = withdrawService.processWithdrawSync(request);
            log.info("Synchronous withdrawal successful: {}", response);
            return ResponseEntity.ok(response);
            
        } catch (WithdrawException e) {
            log.warn("Synchronous withdrawal failed: {} ({})", e.getMessage(), e.getErrorCode());
            return ResponseEntity.badRequest()
                .body(new WithdrawResponse(null, null, "ERROR: " + e.getErrorCode() + ": " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during synchronous withdrawal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new WithdrawResponse(null, null, "ERROR: INTERNAL_ERROR: " + e.getMessage()));
        }
    }

    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}
    