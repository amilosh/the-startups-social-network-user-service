package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.payment.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PremiumValidator;
import school.faang.user_service.validator.UserValidator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumValidator premiumValidator;
    private final UserService userService;
    private final PaymentValidator paymentValidator;
    private final PremiumMapper premiumMapper;
    private final PaymentService paymentService;
    private final UserValidator userValidator;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        userValidator.validateUserById(userId);
        premiumValidator.validateUserIsNotPremium(userId);
        PaymentResponse response = paymentService.sentPayment(premiumPeriod);
        paymentValidator.checkIfPaymentSuccess(response);
        Premium premium = createPremium(userId, premiumPeriod);
        premiumRepository.save(premium);
        return premiumMapper.toDto(premium);
    }

    private Premium createPremium(long userId, PremiumPeriod premiumPeriod) {
        userValidator.validateUserById(userId);
        return Premium.builder()
                .user(userService.findUserById(userId))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(premiumPeriod.getDays()))
                .build();
    }
}
