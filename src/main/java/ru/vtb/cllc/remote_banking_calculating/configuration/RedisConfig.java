package ru.vtb.cllc.remote_banking_calculating.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.vtb.cllc.remote_banking_calculating.model.Record;

@Configuration
@EnableRedisRepositories(basePackages = "ru.vtb.cllc.remote_banking_calculating.dao.redis")
public class RedisConfig {

    @Value("${cllc.redis.hostname")
    private String hostName;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Record> redisTemplate(RedisSerializer redisSerializer) {
        RedisTemplate<String, Record> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
