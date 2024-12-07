package school.faang.user_service.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


@Slf4j
@Configuration
public class RedisConfig {
    @Bean
    public RedisMessageListenerContainer redisContainerConfig(
            RedisConnectionFactory connectionFactory,
            UserBanSubscriber userBanSubscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        MessageListenerAdapter listenerAdapter = createListenerAdapter(userBanSubscriber);

        container.addMessageListener(listenerAdapter, new PatternTopic("user_ban"));

        return container;
    }

    private MessageListenerAdapter createListenerAdapter(UserBanSubscriber userBanSubscriber) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(userBanSubscriber);
        listenerAdapter.setDefaultListenerMethod("onMessage");
        return listenerAdapter;
    }
}