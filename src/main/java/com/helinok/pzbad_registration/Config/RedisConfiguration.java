package com.helinok.pzbad_registration.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.helinok.pzbad_registration.Dtos.GetAllUsersAdminDto;
import com.helinok.pzbad_registration.Dtos.GetAllUsersDto;
import com.helinok.pzbad_registration.Dtos.TournamentDto;
import com.helinok.pzbad_registration.Dtos.UserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class RedisConfiguration {

    private final ObjectMapper redisObjectMapper;

    public RedisConfiguration(@Qualifier("redisObjectMapper")
                              ObjectMapper redisObjectMapper) {
        this.redisObjectMapper = redisObjectMapper;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        Jackson2JsonRedisSerializer<UserDto> userDtoSerializer = createSerializer(UserDto.class);
        Jackson2JsonRedisSerializer<TournamentDto> tournamentDtoSerializer = createSerializer(TournamentDto.class);

        Jackson2JsonRedisSerializer<List> userListSerializer = createListSerializer(GetAllUsersDto.class);
        Jackson2JsonRedisSerializer<List> userAdminListSerializer = createListSerializer(GetAllUsersAdminDto.class);
        Jackson2JsonRedisSerializer<List> tournamentListSerializer = createListSerializer(TournamentDto.class);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("users:dto",
                    createConfig(userDtoSerializer, Duration.ofMinutes(10)))
                .withCacheConfiguration("users:listAll",
                    createConfig(userListSerializer, Duration.ofMinutes(10)))
                .withCacheConfiguration("users:listAllAdmin",
                    createConfig(userAdminListSerializer, Duration.ofMinutes(10)))
                .withCacheConfiguration("tournaments:dto",
                    createConfig(tournamentDtoSerializer, Duration.ofMinutes(10)))
                .withCacheConfiguration("tournaments:listAll",
                    createConfig(tournamentListSerializer, Duration.ofMinutes(10)))
                .withCacheConfiguration("tournaments:listAllUpcoming",
                    createConfig(tournamentListSerializer, Duration.ofMinutes(10)))
                .build();
    }

    private <T> Jackson2JsonRedisSerializer<T> createSerializer(Class<T> clazz) {
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(redisObjectMapper, clazz);
        return serializer;
    }

    private Jackson2JsonRedisSerializer<List> createListSerializer(Class<?> elementType) {
        CollectionType listType = redisObjectMapper.getTypeFactory()
                .constructCollectionType(List.class, elementType);
        Jackson2JsonRedisSerializer<List> serializer = new Jackson2JsonRedisSerializer<>(redisObjectMapper, listType);
        return serializer;
    }

    private RedisCacheConfiguration createConfig(Jackson2JsonRedisSerializer<?> serializer, Duration ttl) {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }
}
