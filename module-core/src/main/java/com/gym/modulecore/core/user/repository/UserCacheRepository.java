package com.gym.modulecore.core.user.repository;

import com.gym.modulecore.core.user.model.dto.User;
import com.gym.modulecore.exception.CommunityException;
import com.gym.modulecore.exception.ErrorCode;
import com.gym.modulecore.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setUser(User user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {} , {}", key, user);
        redisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
    }

    public Optional<User> getUser(String userName) {
        String key = getKey(userName);
        User user = ClassUtils.getSafeCastInstance(redisTemplate.opsForValue().get(key), User.class)
                .orElseThrow(() -> new CommunityException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed"));
        log.info("Get User to Redis {} , {}", key, user);
        return Optional.ofNullable(user);
    }

    private String getKey(String userName) {
        return "USER:" + userName;
    }
}
