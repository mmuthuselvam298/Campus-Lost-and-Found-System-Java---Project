package com.campusfind.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe in-memory sliding window rate limiter for protecting sensitive endpoints.
 */
@Service
public class RateLimitingService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingService.class);

    @Value("${campusfind.rate-limiting.enabled:true}")
    private boolean enabled;

    @Value("${campusfind.rate-limiting.auth-limit-per-minute:20}")
    private int authLimit;

    @Value("${campusfind.rate-limiting.ai-limit-per-minute:15}")
    private int aiLimit;

    @Value("${campusfind.rate-limiting.general-limit-per-minute:100}")
    private int generalLimit;

    private final Map<String, WindowCounter> requestCounts = new ConcurrentHashMap<>();

    public boolean allowAuthRequest(String clientKey) {
        if (!enabled) return true;
        return allow("AUTH:" + clientKey, authLimit);
    }

    public boolean allowAiRequest(String clientKey) {
        if (!enabled) return true;
        return allow("AI:" + clientKey, aiLimit);
    }

    public boolean allowClaimRequest(String clientKey) {
        if (!enabled) return true;
        return allow("CLAIM:" + clientKey, 30);
    }

    private boolean allow(String key, int maxRequests) {
        long currentMinute = System.currentTimeMillis() / 60000;
        String bucketKey = key + ":" + currentMinute;

        WindowCounter counter = requestCounts.computeIfAbsent(bucketKey, k -> new WindowCounter());
        int count = counter.incrementAndGet();

        // Cleanup old keys periodically
        if (requestCounts.size() > 5000) {
            cleanupOldBuckets(currentMinute);
        }

        if (count > maxRequests) {
            logger.warn("Rate limit exceeded for key '{}' ({} > {})", key, count, maxRequests);
            return false;
        }
        return true;
    }

    private void cleanupOldBuckets(long currentMinute) {
        requestCounts.keySet().removeIf(k -> {
            int lastColon = k.lastIndexOf(':');
            if (lastColon != -1) {
                try {
                    long minute = Long.parseLong(k.substring(lastColon + 1));
                    return minute < currentMinute - 2;
                } catch (NumberFormatException ignored) {}
            }
            return false;
        });
    }

    private static class WindowCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        public int incrementAndGet() {
            return count.incrementAndGet();
        }
    }
}
