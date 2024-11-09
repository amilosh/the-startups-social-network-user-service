package school.faang.user_service.exception.premium;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PremiumErrorMessage {
    public static final String USER_ALREADY_HAS_PREMIUM = "THE USER WITH ID: %S ALREADY HAS A PREMIUM SUBSCRIPTION BEFORE: %S";
    public static final String UNSUCCESSFUL_PREMIUM_PAYMENT = "PAYMENT BY PERIOD: %S FOR USER ID: %S UNSUCCESSFUL";
    public static final String INVALID_PREMIUM_PERIOD = "PREMIUM PERIOD IN %S DAYS NOT FOUND, PLEASE SELECT AMONG: %S";
    public static final String NO_RESPONSE = "NO RESPONSE FROM PAYMENT SERVICE.";
}