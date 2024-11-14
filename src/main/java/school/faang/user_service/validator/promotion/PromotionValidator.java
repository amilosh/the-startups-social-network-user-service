package school.faang.user_service.validator.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.promotion.PromotionValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static school.faang.user_service.exception.premium.PremiumErrorMessage.NO_RESPONSE;
import static school.faang.user_service.exception.promotion.PromotionErrorMessages.EVENT_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.exception.promotion.PromotionErrorMessages.USER_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.exception.promotion.PromotionErrorMessages.USER_NOT_OWNER_OF_EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionValidator {
    public void checkUserForPromotion(User user) {
        log.info("Verification of User with id: {} for buying promotion", user.getId());
        Optional.ofNullable(user.getPromotion())
                .ifPresent(promotion -> {
                    if (promotion.getNumberOfViews() > 0) {
                        throw new PromotionValidationException(USER_ALREADY_HAS_PROMOTION,
                                user.getId(),
                                promotion.getNumberOfViews());
                    }
                });
    }

    public void checkEventForUserAndPromotion(long userId, Event event) {
        log.info("Verification of Event with id: {} for buying promotion", event.getId());
        if (userId != event.getOwner().getId()) {
            throw new PromotionValidationException(USER_NOT_OWNER_OF_EVENT, userId, event.getId());
        }

        Optional.ofNullable(event.getPromotion())
                .ifPresent(promotion -> {
                    if (promotion.getNumberOfViews() > 0) {
                        throw new PromotionValidationException(EVENT_ALREADY_HAS_PROMOTION,
                                event.getId(),
                                promotion.getNumberOfViews());
                    }
                });
    }

    public List<UserPromotion> getActiveUserPromotions(List<User> users) {
        return Optional.ofNullable(users)
                .orElse(Collections.emptyList())
                .stream()
                .map(user -> Optional.ofNullable(user.getPromotion()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .toList();
    }

    public List<EventPromotion> getActiveEventPromotions(List<Event> events) {
        return Optional.ofNullable(events)
                .orElse(Collections.emptyList())
                .stream()
                .map(event -> Optional.ofNullable(event.getPromotion()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .toList();
    }

    public void checkPromotionPaymentResponse(PaymentResponseDto paymentResponse, long id, PromotionTariff tariff,
                                              String errorMessage) {
        log.info("Check promotion payment response: {}", paymentResponse);
        checkIsPaymentNull(paymentResponse);
        if (!paymentResponse.getStatus().equals(PaymentStatus.SUCCESS)) {
            throw new UnSuccessPaymentException(errorMessage, tariff.getNumberOfViews(), id, paymentResponse.getMessage());
        }
    }

    private void checkIsPaymentNull(PaymentResponseDto paymentResponse) {
        if (paymentResponse == null) {
            log.error("No response from payment service for payment");
            throw new UnSuccessPaymentException(NO_RESPONSE);
        }
    }
}
