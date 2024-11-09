package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.promotion.EventPromotionResponseDto;
import school.faang.user_service.dto.promotion.UserPromotionResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.mapper.promotion.EventPromotionMapper;
import school.faang.user_service.mapper.promotion.UserPromotionMapper;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.validation.event.EventServiceValidator;
import school.faang.user_service.validator.promotion.PromotionValidator;
import school.faang.user_service.validator.user.UserValidator;

import static school.faang.user_service.exception.promotion.PromotionErrorMessages.UNSUCCESSFUL_EVENT_PROMOTION_PAYMENT;
import static school.faang.user_service.exception.promotion.PromotionErrorMessages.UNSUCCESSFUL_USER_PROMOTION_PAYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {
    private final UserPromotionRepository userPromotionRepository;
    private final EventPromotionRepository eventPromotionRepository;
    private final PaymentService paymentService;
    private final PromotionValidator promotionValidator;
    private final UserValidator userValidator;
    private final EventServiceValidator eventServiceValidator;
    private final PromotionBuilder promotionBuilder;
    private final UserPromotionMapper userPromotionMapper;
    private final EventPromotionMapper eventPromotionMapper;

    @Transactional
    public UserPromotionResponseDto buyPromotion(long userId, PromotionTariff tariff) {
        log.info("Attempt to purchase promotion tariff {} by user with id: {}", tariff.toString(), userId);

        User user = userValidator.validateUser(userId);
        log.debug("User retrieved: {}", user);

        promotionValidator.checkUserForPromotion(user);

        PaymentResponseDto paymentResponse = paymentService.sendPayment(tariff);
        log.info("Payment sent for userId: {} with amount: {}", userId, tariff.getCost());

        promotionValidator
                .checkPromotionPaymentResponse(paymentResponse, userId, tariff, UNSUCCESSFUL_USER_PROMOTION_PAYMENT);
        UserPromotion userPromotion = promotionBuilder.buildUserPromotion(user, tariff);

        userPromotion = userPromotionRepository.save(userPromotion);
        log.debug("Promotion saved for userId: {} with paymentNumber: {}", userId, paymentResponse.getPaymentNumber());

        return userPromotionMapper.toDto(userPromotion);
    }

    @Transactional
    public EventPromotionResponseDto buyEventPromotion(long userId, long eventId, PromotionTariff tariff) {
        log.info("Attempting to purchase promotion tariff {} by User with id: {} for event id: {}", tariff.toString(),
                userId, eventId);

        Event event = eventServiceValidator.validateEventId(eventId);
        log.debug("Event retrieved: {}", event);

        promotionValidator.checkEventForUserAndPromotion(userId, event);

        PaymentResponseDto paymentResponse = paymentService.sendPayment(tariff);
        log.info("Payment sent from userId {} for eventId {} with amount: {}", userId, eventId, tariff.getCost());

        promotionValidator
                .checkPromotionPaymentResponse(paymentResponse, eventId, tariff, UNSUCCESSFUL_EVENT_PROMOTION_PAYMENT);
        EventPromotion eventPromotion = promotionBuilder.buildEventPromotion(event, tariff);

        eventPromotion = eventPromotionRepository.save(eventPromotion);
        log.debug("Promotion saved for eventId: {} with paymentNumber: {}", eventId, paymentResponse.getPaymentNumber());

        return eventPromotionMapper.toDto(eventPromotion);
    }
}