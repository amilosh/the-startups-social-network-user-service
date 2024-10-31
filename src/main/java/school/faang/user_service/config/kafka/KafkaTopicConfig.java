package school.faang.user_service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.channels.ban-user-channel.name}")
    private String banUserTopic;

    @Value("${spring.kafka.channels.follower-event-channel.name}")
    private String followerEvent;

    @Value("${spring.kafka.channels.event-start-channel.name}")
    private String eventStartTopic;

    @Value("${spring.kafka.channels.mentorship-accepted-event-channel.name}")
    private String mentorshipAcceptedEventTopic;

    @Value("${spring.kafka.channels.recommendation-request-channel.name}")
    private String recommendationReqTopic;

    @Value("${spring.kafka.channels.recommendation-received-channel.name}")
    private String recommendationReceived;

    @Value("${spring.kafka.channels.follow-project-channel.name}")
    private String followProjectTopic;

    @Value("${spring.kafka.channels.premium-bought-channel.name}")
    private String premiumBoughtTopic;

    @Value("${spring.kafka.channels.event-starter}")
    private String eventStarter;

    @Value("${spring.kafka.channels.goal-completed-event-channel.name}")
    private String goalCompletedTopic;

    @Value("${spring.kafka.channels.skill-acquired-channel.name}")
    private String skillAcquired;

    @Value("${spring.kafka.channels.profile}")
    private String profileView;

    @Value("${spring.kafka.channels.skill-offered-channel.name}")
    private String skillOffered;

    @Value("${spring.kafka.channels.mentorship-request-channel.name}")
    private String mentorshipRequestTopic;

    @Value("${spring.kafka.channels.mentorship-start-channel.name}")
    private String mentorshipStart;

    @Value("${spring.kafka.channels.search-appearance-channel.name}")
    private String searchAppearanceTopic;

    @Bean
    public NewTopic banUserTopic() {
        return TopicBuilder
                .name(banUserTopic)
                .build();
    }

    @Bean
    public NewTopic followerEventTopic() {
        return TopicBuilder
                .name(followerEvent)
                .build();
    }

    @Bean
    public NewTopic eventStartTopic() {
        return TopicBuilder
                .name(eventStartTopic)
                .build();
    }

    @Bean
    public NewTopic mentorshipAcceptedEventTopic() {
        return TopicBuilder
                .name(mentorshipAcceptedEventTopic)
                .build();
    }

    @Bean
    public NewTopic recommendationRequestTopic() {
        return TopicBuilder
                .name(recommendationReqTopic).
                build();
    }

    @Bean
    public NewTopic recommendationReceivedTopic() {
        return TopicBuilder
                .name(recommendationReceived)
                .build();
    }

    @Bean
    public NewTopic followProjectTopic() {
        return TopicBuilder
                .name(followProjectTopic)
                .build();
    }

    @Bean
    public NewTopic premiumBoughtTopic() {
        return TopicBuilder
                .name(premiumBoughtTopic)
                .build();
    }

    @Bean
    public NewTopic eventStarterTopic() {
        return TopicBuilder
                .name(eventStarter)
                .build();
    }

    @Bean
    public NewTopic goalCompletedTopic() {
        return TopicBuilder
                .name(goalCompletedTopic)
                .build();
    }

    @Bean
    public NewTopic skillAcquiredTopic() {
        return TopicBuilder
                .name(skillAcquired)
                .build();
    }

    @Bean
    public NewTopic profileViewTopic() {
        return TopicBuilder
                .name(profileView)
                .build();
    }

    @Bean
    public NewTopic skillOfferedTopic() {
        return TopicBuilder
                .name(skillOffered)
                .build();
    }

    @Bean
    public NewTopic mentorshipRequestTopic() {
        return TopicBuilder
                .name(mentorshipRequestTopic)
                .build();
    }

    @Bean
    public NewTopic mentorshipStartTopic() {
        return TopicBuilder
                .name(mentorshipStart)
                .build();
    }

    @Bean
    public NewTopic searchAppearanceTopic() {
        return TopicBuilder
                .name(searchAppearanceTopic)
                .build();
    }

}
