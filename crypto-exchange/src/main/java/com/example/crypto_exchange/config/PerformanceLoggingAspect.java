package com.example.crypto_exchange.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceLoggingAspect {
    
    private static final long SLOW_METHOD_THRESHOLD_MS = 1000; // 1 second
    
    @Around("execution(* com.example.crypto_exchange.service.*.*(..))")
    public Object logServiceMethodPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodPerformance(joinPoint, "SERVICE");
    }
    
    @Around("execution(* com.example.crypto_exchange.controller.*.*(..))")
    public Object logControllerMethodPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodPerformance(joinPoint, "CONTROLLER");
    }
    
    private Object logMethodPerformance(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            if (duration > SLOW_METHOD_THRESHOLD_MS) {
                log.warn("SLOW {} method: {}.{}() took {}ms", layer, className, methodName, duration);
            } else {
                log.debug("{} method: {}.{}() took {}ms", layer, className, methodName, duration);
            }
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("{} method failed: {}.{}() failed after {}ms with error: {}", 
                    layer, className, methodName, duration, e.getMessage());
            throw e;
        }
    }
} 