package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.message.consumer.BanUserEventListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.channels.ban-user-channel.name}")
    private String banUserTopicName;

    private final BanUserEventListener banUserEventListener;

    @Bean
    public MessageListenerAdapter banUserEventListenerAdapter(BanUserEventListener banUserEventListener) {
        return new MessageListenerAdapter(banUserEventListener);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(banUserEventListener, banUserTopic());
        return container;
    }

    @Bean("banUserTopic")
    public ChannelTopic banUserTopic() {
        return new ChannelTopic(banUserTopicName);
    }
}
