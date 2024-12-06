package school.faang.user_service.service.user;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.redis.events.PremiumBoughtEvent;
import school.faang.user_service.config.redis.publisher.PremiumBoughtEventPublisher;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exceptions.PremiumSubscriptionException;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumSubscriptionService {

    private final PremiumBoughtEventPublisher eventPublisher;
    private final PremiumRepository premiumRepository;
    public void buyPremium(Long userId, Double amount, Integer duration) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endDate = now.plusDays(duration);

            Premium premium = Premium.builder()
                    .user(User.builder().id(userId).build())
                    .startDate(now)
                    .endDate(endDate)
                    .build();

            premiumRepository.save(premium);


            PremiumBoughtEvent event = PremiumBoughtEvent.builder()
                    .userId(userId)
                    .amount(amount)
                    .duration(duration)
                    .timestamp(now)
                    .build();

            eventPublisher.publish(event);
            log.info("Premium subscription purchased for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to publish PremiumBoughtEvent for user: {}", userId, e);
            throw new PremiumSubscriptionException("Failed to process premium subscription");
        }
    }
}

