package com.example.securitypoc.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {
    private final int maxRequests;
    private final long windowSeconds;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitService(@Value("${rate-limit.max-requests}") int maxRequests,
                            @Value("${rate-limit.window-seconds}") long windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    public Result consume(String key) {
        long now = Instant.now().getEpochSecond();
        Window window = windows.compute(key, (ignored, current) -> {
            if (current == null || now >= current.resetAt) {
                return new Window(new AtomicInteger(1), now + windowSeconds);
            }
            current.count.incrementAndGet();
            return current;
        });

        int used = window.count.get();
        int remaining = Math.max(0, maxRequests - used);
        boolean allowed = used <= maxRequests;
        return new Result(allowed, remaining, Math.max(1, window.resetAt - now));
    }

    private record Window(AtomicInteger count, long resetAt) {}
    public record Result(boolean allowed, int remaining, long retryAfterSeconds) {}
}
