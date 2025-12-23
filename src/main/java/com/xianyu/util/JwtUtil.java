package com.xianyu.util;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public final class JwtUtil {

    private JwtUtil() {
    }

    public static Optional<String> generateToken(String subject, long ttlSeconds) {
        if (subject == null || subject.isBlank()) {
            return Optional.empty();
        }
        // 占位实现：实际项目中请替换为具体的 JWT 签发逻辑
        String token = subject + "." + Instant.now().getEpochSecond() + "." + ttlSeconds;
        return Optional.of(token);
    }

    public static boolean validateToken(String token) {
        return token != null && !token.isBlank();
    }

    public static Date getExpiration(long ttlSeconds) {
        return Date.from(Instant.now().plusSeconds(ttlSeconds));
    }
}

