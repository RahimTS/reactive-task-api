package com.rahim.reactive_task_api.service;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class ErrorHandlingExamples {

    // 1. Fallback value
    public Mono<String> withFallback() {
        return fetchData()
                .onErrorReturn("Default value");
    }

    // 2. Fallback Publisher
    public Mono<String> withFallbackPublisher() {
        return fetchData()
                .onErrorResume(error -> {
                    System.err.println("Primary failed: " + error.getMessage());
                    return fetchBackupData();
                });
    }

    // 3. Retry with exponential backoff
    public Mono<String> withRetry() {
        return fetchData()
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                        .doBeforeRetry(signal -> System.out.println("Retry attempt: " + signal.totalRetries())));
    }

    // 4. Timeout
    public Mono<String> withTimeout() {
        return fetchData()
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(TimeoutException.class, e -> Mono.just("Request timed out"));
    }

    // 5. Circuit breaker pattern (with Resilience4j)
    public Mono<String> withCircuitBreaker() {
        // Would use Resilience4j here
        return fetchData();
    }

    private Mono<String> fetchData() {
        return Mono.error(new RuntimeException("Service unavailable"));
    }

    private Mono<String> fetchBackupData() {
        return Mono.just("Backup data");
    }
}