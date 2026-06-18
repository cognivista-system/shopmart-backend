package com.shopmart.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Caching is enabled everywhere via @EnableCaching.
 * - Default profile: in-memory ConcurrentMapCacheManager (spring.cache.type=simple). No server needed,
 *   no serialization, bullet-proof.
 * - 'redis' profile: this RedisCacheManager (JSON values, 10-min TTL). Run with --spring.profiles.active=redis.
 * Cached regions: "products", "categories", "brands" (evicted on writes).
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Profile("redis")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                               ObjectMapper objectMapper) {
        // Embed type info so cached DTOs (records, Instant fields) round-trip correctly.
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();
        ObjectMapper redisMapper = objectMapper.copy();
        redisMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisMapper)));
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }
}
