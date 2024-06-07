package com.house.hunter.util;

import com.house.hunter.model.pojo.BlacklistedToken;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
@Component
@AllArgsConstructor
public class BlacklistedTokenService {
    private final RedisTemplate<String, BlacklistedToken> redisTemplate;

    public void addToBlacklist(String token, Instant expiryDate) {
        long expiryTimestamp = expiryDate.toEpochMilli();
        BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiryTimestamp);
        redisTemplate.opsForValue().set(token, blacklistedToken, Duration.between(Instant.now(), expiryDate));
    }

    public boolean isBlacklisted(String token) {
        BlacklistedToken blacklistedToken = redisTemplate.opsForValue().get(token);
        if (blacklistedToken != null) {
            long expiryTimestamp = blacklistedToken.getExpiryTimestamp();
            return Instant.now().isBefore(Instant.ofEpochMilli(expiryTimestamp));
        }
        return false;
    }
}

