package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PremiumValidator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumValidator premiumValidator;
    private final UserService userService;
    private final PaymentValidator paymentValidator;
    private final PremiumMapper premiumMapper;

    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        premiumValidator.validateUserIsNotPremium(userId);
        paymentValidator.checkIfPaymentSuccess();
        Premium premium = createPremium(userId, premiumPeriod);
        return save(premium);
    }

    public PremiumDto save(Premium premium) {
        return premiumMapper.toDto(premiumRepository.save(premium));
    }

    private Premium createPremium(long userId, PremiumPeriod premiumPeriod) {
        return Premium.builder()
                .user(userService.findUserById(userId))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(premiumPeriod.getDays()))
                .build();
    }
}
