package school.faang.user_service.service.user;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.redis.events.PremiumBoughtEvent;
import school.faang.user_service.config.redis.publisher.PremiumBoughtEventPublisher;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumSubscriptionService {

    private final PremiumBoughtEventPublisher eventPublisher;

    public void buyPremium(Long userId, Double amount, Integer duration) {
        try {

            PremiumBoughtEvent event = PremiumBoughtEvent.builder()
                    .userId(userId)
                    .amount(amount)
                    .duration(duration)
                    .timestamp(LocalDateTime.now())
                    .build();


            eventPublisher.publish(event);
            log.info("Premium subscription purchased for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to publish PremiumBoughtEvent for user: {}", userId, e);
        }
    }
}

