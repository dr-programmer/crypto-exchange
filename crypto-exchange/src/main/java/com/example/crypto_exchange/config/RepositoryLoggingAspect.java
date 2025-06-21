package com.example.crypto_exchange.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class RepositoryLoggingAspect {
    
    @Around("execution(* com.example.crypto_exchange.repository.*.*(..))")
    public Object logRepositoryOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        
        log.debug("Repository operation: {}.{}() with args: {}", className, methodName, Arrays.toString(args));
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            if (result != null) {
                if (result instanceof java.util.Collection) {
                    log.debug("Repository operation completed: {}.{}() returned {} items in {}ms", 
                            className, methodName, ((java.util.Collection<?>) result).size(), duration);
                } else {
                    log.debug("Repository operation completed: {}.{}() returned {} in {}ms", 
                            className, methodName, result.getClass().getSimpleName(), duration);
                }
            } else {
                log.debug("Repository operation completed: {}.{}() returned null in {}ms", 
                        className, methodName, duration);
            }
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Repository operation failed: {}.{}() after {}ms with error: {}", 
                    className, methodName, duration, e.getMessage(), e);
            throw e;
        }
    }
} 