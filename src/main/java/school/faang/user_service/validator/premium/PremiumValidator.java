package school.faang.user_service.validator.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.premium.ExistingPurchaseException;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static school.faang.user_service.exception.premium.PremiumErrorMessage.NO_RESPONSE;
import static school.faang.user_service.exception.premium.PremiumErrorMessage.UNSUCCESSFUL_PREMIUM_PAYMENT;
import static school.faang.user_service.exception.premium.PremiumErrorMessage.USER_ALREADY_HAS_PREMIUM;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumValidator {
    public void validateUserForSubPeriod(User user) {
        log.info("Verification of User with id: {} for buying premium subscription", user.getId());
        Optional.ofNullable(user.getPremium())
                .ifPresent(premium -> {
                    if (premium.getEndDate().isAfter(now())) {
                        throw new ExistingPurchaseException(USER_ALREADY_HAS_PREMIUM,
                                user.getId(),
                                premium.getEndDate());
                    }
                });
    }

    public void checkPaymentResponse(PaymentResponseDto paymentResponse, long userId, PremiumPeriod period) {
        log.info("Check premium payment response: {}", paymentResponse);
        checkIsPaymentNull(paymentResponse, userId);
        if (!paymentResponse.getStatus().equals(PaymentStatus.SUCCESS)) {
            throw new UnSuccessPaymentException(UNSUCCESSFUL_PREMIUM_PAYMENT, userId, period.getDays());
        }
    }

    private void checkIsPaymentNull(PaymentResponseDto paymentResponse, long userId) {
        if (paymentResponse == null) {
            log.error("No response from payment service for payment with user ID: {}", userId);
            throw new UnSuccessPaymentException(NO_RESPONSE);
        }
    }
}
