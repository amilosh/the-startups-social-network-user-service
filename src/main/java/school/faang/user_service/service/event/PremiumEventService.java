package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.redis.events.PremiumBoughtEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumEventService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;

    @Transactional
    public void processEvent(PremiumBoughtEvent event) {
        log.info("Processing PremiumBoughtEvent: {}", event);


        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + event.getUserId()));


        Premium premium = premiumRepository.findByUserId(user.getId()).orElse(new Premium());


        premium.setUser(user);
        premium.setStartDate(LocalDateTime.now());
        premium.setEndDate(LocalDateTime.now().plusDays(event.getDuration()));


        premiumRepository.save(premium);

        log.info("Updated Premium subscription for user ID: {}", user.getId());
    }
}

