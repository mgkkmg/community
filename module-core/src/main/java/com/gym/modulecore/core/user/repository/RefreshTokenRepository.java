package com.gym.modulecore.core.user.repository;

import com.gym.modulecore.core.user.model.dto.TokenInfo;
import com.gym.modulecore.exception.CommunityException;
import com.gym.modulecore.exception.ErrorCode;
import com.gym.modulecore.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setRefreshToken(TokenInfo tokenInfo) {
        String key = getKey(tokenInfo.getUserName());
        log.info("Set RefreshToken to Redis {} , {}", key, tokenInfo.getRefreshToken());
        redisTemplate.opsForValue().set(key, tokenInfo.getRefreshToken());
        redisTemplate.expire(key, 60L, TimeUnit.SECONDS); // 테스트를 위한 60초 지정
    }

    public Optional<TokenInfo> getRefreshToken(String userName) {
        String key = getKey(userName);
        String refreshToken = ClassUtils.getSafeCastInstance(redisTemplate.opsForValue().get(key), String.class)
                .orElseThrow(() -> new CommunityException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to String class failed"));
        log.info("Get RefreshToken to Redis {} , {}", key, refreshToken);
        return Optional.ofNullable(TokenInfo.builder()
                .refreshToken(refreshToken)
                .userName(userName)
                .build());
    }

    public void deleteRefreshToken(String userName) {
        String key = getKey(userName);
        log.info("Delete RefreshToken to Redis {} , {}", key);
        // Refresh Token을 삭제
        redisTemplate.delete(key);
    }

    private String getKey(String userName) {
        return "RT:" + userName;
    }
}
