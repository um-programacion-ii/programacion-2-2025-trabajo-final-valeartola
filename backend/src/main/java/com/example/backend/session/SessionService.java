package com.example.backend.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "session:";

    public void saveSession(UserSession session) {
        redisTemplate.opsForValue().set(PREFIX + session.getSessionId(), session);
        redisTemplate.expire(PREFIX + session.getSessionId(), Duration.ofMinutes(30));
    }

    public UserSession getSession(String sessionId) {
        return (UserSession) redisTemplate.opsForValue().get(PREFIX + sessionId);
    }

    public UserSession updateSession(String sessionId, UserSession newData) {
        String key = PREFIX + sessionId;
        redisTemplate.opsForValue().set(key, newData);
        redisTemplate.expire(key, Duration.ofMinutes(30));
        return newData;
    }

    public void deleteSession(String sessionId) {
        redisTemplate.delete(PREFIX + sessionId);
    }
}
