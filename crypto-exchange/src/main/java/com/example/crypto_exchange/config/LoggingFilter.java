package com.example.crypto_exchange.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            log.info("Incoming request: {} {} from {}", 
                request.getMethod(), 
                request.getRequestURI(),
                request.getRemoteAddr());
            
            log.debug("Request headers: {}", Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                    headerName -> headerName,
                    request::getHeader
                )));

            filterChain.doFilter(requestWrapper, responseWrapper);

            String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

            log.info("Response status: {} for {} {}", 
                response.getStatus(), 
                request.getMethod(), 
                request.getRequestURI());
            
            log.debug("Request body: {}", requestBody);
            log.debug("Response body: {}", responseBody);

        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }
} 