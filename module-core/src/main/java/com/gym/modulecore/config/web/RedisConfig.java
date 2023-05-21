package com.gym.modulecore.config.web;

//import com.gym.modulecore.core.user.model.User;
//import io.lettuce.core.RedisURI;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConfiguration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;

//@RequiredArgsConstructor
//@EnableRedisRepositories
//@Configuration
public class RedisConfig {

//    private final RedisProperties redisProperties;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisURI redisURI = RedisURI.create(redisProperties.getUrl());
//        RedisConfiguration redisConfiguration = LettuceConnectionFactory.createRedisConfiguration(redisURI);
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration);
//        factory.afterPropertiesSet();
//        return factory;
//    }
//
//    @Bean
//    public RedisTemplate<String, User> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));
//
//        return redisTemplate;
//    }
}
