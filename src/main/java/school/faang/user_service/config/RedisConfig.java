package school.faang.user_service.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
@Configuration
@Slf4j
public class RedisConfig {

    private final RedisProperties redisProperties;


    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        log.info("Создание LettuceConnectionFactory для Redis с хостом: {} и портом:{}", redisProperties.getHost(), redisProperties.getPort());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        log.info("LettuceConnectionFactory успешно создан");
        return connectionFactory;
    }

    @Bean
    public ChannelTopic followerChannel() {
        log.info("Создание ChannelTopic для канала: {}", redisProperties.getFollowerChannel());
        return new ChannelTopic(redisProperties.getFollowerChannel());
    }

    @Bean
    public ChannelTopic unfollowerChannel() {
        log.info("Создание ChannelTopic для канала: {}", redisProperties.getUnfollowerChannel());
        return new ChannelTopic(redisProperties.getUnfollowerChannel());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("Создание RedisTemplate с кастомными сериализаторами.");

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        log.info("RedisTemplate создан и сериализаторы настроены.");

        return redisTemplate;
    }
}
