package school.faang.user_service.validator.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.promotion.PromotionValidationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.exception.promotion.PromotionErrorMessages.EVENT_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.exception.promotion.PromotionErrorMessages.USER_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.exception.promotion.PromotionErrorMessages.USER_NOT_OWNER_OF_EVENT;
import static school.faang.user_service.service.premium.util.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.service.promotion.util.PromotionFabric.ACTIVE_NUMBER_OF_VIEWS;
import static school.faang.user_service.service.promotion.util.PromotionFabric.buildActiveUserPromotion;
import static school.faang.user_service.service.promotion.util.PromotionFabric.buildEventWithActivePromotion;
import static school.faang.user_service.service.promotion.util.PromotionFabric.buildNonActiveUserPromotion;
import static school.faang.user_service.service.promotion.util.PromotionFabric.buildUserWithActivePromotion;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getEvent;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getUser;
import static school.faang.user_service.service.promotion.util.PromotionFabric.getUserPromotion;

@ExtendWith(MockitoExtension.class)
class PromotionValidatorTest {
    private static final long USER_ID = 1;
    private static final long SECOND_USER_ID = 2;
    private static final long THIRD_USER_ID = 3;
    private static final long EVENT_ID = 1;
    private static final long PROMOTION_ID = 1;
    private static final long SECOND_PROMOTION_ID = 2;
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;
    private static final String MESSAGE = "test message";

    @InjectMocks
    private PromotionValidator promotionValidator;

    @Test
    @DisplayName("Given already have active promotion user when check then throw exception")
    void testCheckUserForPromotionAlreadyHavePromotion() {
        User user = buildUserWithActivePromotion(USER_ID);
        assertThatThrownBy(() -> promotionValidator.checkUserForPromotion(user))
                .isInstanceOf(PromotionValidationException.class)
                .hasMessageContaining(USER_ALREADY_HAS_PROMOTION, user.getId(), ACTIVE_NUMBER_OF_VIEWS);
    }

    @Test
    @DisplayName("Check user for promotion successful")
    void testCheckUserForPromotionSuccessful() {
        User user = getUser(USER_ID);
        user.setPromotion(getUserPromotion(PROMOTION_ID));
        promotionValidator.checkUserForPromotion(user);
    }

    @Test
    @DisplayName("Get active user promotions success")
    void testGetActiveUserPromotionsSuccess() {
        UserPromotion activePromotion1 = buildActiveUserPromotion(PROMOTION_ID);
        UserPromotion activePromotion2 = buildActiveUserPromotion(SECOND_PROMOTION_ID);
        UserPromotion unActivePromotion = buildNonActiveUserPromotion(PROMOTION_ID);

        User userWithActivePromotion1 = getUser(USER_ID);
        User userWithActivePromotion2 = getUser(SECOND_USER_ID);
        User userWithNonActivePromotion = getUser(THIRD_USER_ID);

        userWithActivePromotion1.setPromotion(activePromotion1);
        userWithActivePromotion2.setPromotion(activePromotion2);
        userWithNonActivePromotion.setPromotion(unActivePromotion);

        List<User> users = List.of(userWithActivePromotion1, userWithNonActivePromotion, userWithActivePromotion2);
        List<UserPromotion> expectedActivePromotions = List.of(activePromotion1, activePromotion2);

        assertThat(promotionValidator.getActiveUserPromotions(users))
                .containsExactlyInAnyOrderElementsOf(expectedActivePromotions);
    }


    @Test
    @DisplayName("Given not owner user when check then throw exception")
    void testCheckEventForUserAndPromotionUserNotOwner() {
        User user = getUser(USER_ID);
        Event event = getEvent(EVENT_ID, user);

        assertThatThrownBy(() ->
                promotionValidator.checkEventForUserAndPromotion(SECOND_USER_ID, event))
                .isInstanceOf(PromotionValidationException.class)
                .hasMessageContaining(USER_NOT_OWNER_OF_EVENT, SECOND_USER_ID, EVENT_ID);
    }

    @Test
    @DisplayName("Given event with active promotion when check then throw exception")
    void testCheckEventForUserAndPromotionActivePromotion() {
        User user = getUser(USER_ID);
        Event event = buildEventWithActivePromotion(EVENT_ID);
        event.setOwner(user);

        assertThatThrownBy(() -> promotionValidator.checkEventForUserAndPromotion(USER_ID, event))
                .isInstanceOf(PromotionValidationException.class)
                .hasMessageContaining(EVENT_ALREADY_HAS_PROMOTION, EVENT_ID, ACTIVE_NUMBER_OF_VIEWS);
    }

    @Test
    @DisplayName("Given unsuccessful payment response when check then throw exception")
    void testCheckPromotionPaymentResponseUnsuccessfulPayment() {
        PaymentResponseDto paymentResponse = getPaymentResponse(PaymentStatus.FAILED, MESSAGE);

        assertThatThrownBy(() ->
                promotionValidator.checkPromotionPaymentResponse(paymentResponse, USER_ID, TARIFF, MESSAGE))
                .isInstanceOf(UnSuccessPaymentException.class)
                .hasMessageContaining(MESSAGE, TARIFF.getNumberOfViews(), USER_ID, MESSAGE);
    }
}
