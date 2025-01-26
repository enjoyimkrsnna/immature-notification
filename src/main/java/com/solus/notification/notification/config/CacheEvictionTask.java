package com.solus.notification.notification.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheEvictionTask {

    @Scheduled(cron = "0 0/1 * * * *")  // Runs every 1 minute
    @CacheEvict(value = "notifications", key = "'interval-notifications'")
    public void evictNotificationCache() {
        System.out.println("Cache cleared for 'interval-notifications' at: " + java.time.Instant.now());
    }
}

