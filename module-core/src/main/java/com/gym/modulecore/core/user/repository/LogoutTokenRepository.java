package com.gym.modulecore.core.user.repository;

import com.gym.modulecore.exception.CommunityException;
import com.gym.modulecore.exception.ErrorCode;
import com.gym.modulecore.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LogoutTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setLogoutToken(String accessToken, Long accessExpiredTimeMs) {
        String key = getKey(accessToken);
        log.info("Set AccessToken to Redis {} , {}", key, accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", accessExpiredTimeMs, TimeUnit.MILLISECONDS);
    }

    public boolean isLogoutToken(String accessToken) {
        String key = getKey(accessToken);
        String blackListToken = ClassUtils.getSafeCastInstance(redisTemplate.opsForValue().get(key), String.class)
                .orElseThrow(() -> new CommunityException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to String class failed"));
        log.info("Get AccessToken to Redis {} , {}", key, blackListToken);
        if (StringUtils.hasText(blackListToken) && "logout".equals(blackListToken)) {
            return true;
        }
        return false;
    }

    private String getKey(String userName) {
        return "AT:" + userName;
    }
}
