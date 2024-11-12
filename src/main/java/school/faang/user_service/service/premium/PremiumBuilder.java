package school.faang.user_service.service.premium;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;

import static java.time.LocalDateTime.now;

@Component
public class PremiumBuilder {
    public Premium buildPremium(User user, PremiumPeriod premiumPeriod) {
        return Premium.builder()
                .user(user)
                .startDate(now())
                .endDate(now().plusDays(premiumPeriod.getDays()))
                .build();
    }
}
