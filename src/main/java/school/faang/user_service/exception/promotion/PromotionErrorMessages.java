package school.faang.user_service.exception.promotion;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PromotionErrorMessages {
    public static final String PROMOTION_NOT_FOUND = "PROMOTION TARIFF OF %S VIEWS NOT FOUND, PLEASE SELECT BETWEEN: %S";
    public static final String USER_ALREADY_HAS_PROMOTION = "USER WITH ID: %S ALREADY HAS PROMOTION, %S VIEWS LEFT";
    public static final String UNSUCCESSFUL_USER_PROMOTION_PAYMENT = "PAYMENT BY: %S VIEWS PROMOTION FOR USER ID: %S UNSUCCESSFUL. RESPONSE MESSAGE: %S";
    public static final String UNSUCCESSFUL_EVENT_PROMOTION_PAYMENT = "PAYMENT BY: %S VIEWS PROMOTION FOR EVENT ID: %S UNSUCCESSFUL. RESPONSE MESSAGE: %S";
    public static final String EVENT_ALREADY_HAS_PROMOTION = "EVENT WITH ID: %S ALREADY HAS PROMOTION, %S VIEWS LEFT";
    public static final String USER_NOT_OWNER_OF_EVENT = "USER WITH ID: %S NOT OWNER OF EVENT WITH ID: %S";
}