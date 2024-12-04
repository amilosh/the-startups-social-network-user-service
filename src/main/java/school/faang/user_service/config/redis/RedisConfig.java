package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.service.consumer.BanUserSubscriber;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.channels.ban-user-channel.name}")
    private String banUserTopicName;

    private final BanUserSubscriber banUserSubscriber;

    @Bean
    public MessageListenerAdapter messageListenerAdapter(BanUserSubscriber banUserSubscriber) {
        return new MessageListenerAdapter(banUserSubscriber);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(banUserSubscriber, banUserTopic());
        return container;
    }

    @Bean("banUserTopic")
    public ChannelTopic banUserTopic() {
        return new ChannelTopic(banUserTopicName);
    }
}
