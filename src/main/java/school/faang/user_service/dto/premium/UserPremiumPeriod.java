package school.faang.user_service.dto.premium;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

@Getter
public enum UserPremiumPeriod {
    ONE_MONTH_BASIC(30, new BigDecimal(10), "Basic"),
    ONE_MONTH_STANDARD(30, new BigDecimal(15), "Standard"),
    ONE_MONTH_PRO(30, new BigDecimal(20), "Pro"),
    THREE_MONT_BASIC(90, new BigDecimal(25), "Basic"),
    THREE_MONT_STANDARD(90, new BigDecimal(40), "Standard"),
    THREE_MONT_PRO(90, new BigDecimal(50), "Pro"),
    ONE_YEAR_BASIC(365, new BigDecimal(80), "Basic"),
    ONE_YEAR_STANDARD(365, new BigDecimal(80), "Standard"),
    ONE_YEAR_PRO(365, new BigDecimal(80), "Pro");

    private final int days;
    private final BigDecimal amount;
    private final String premiumType;

    UserPremiumPeriod(int days, BigDecimal amount, String premiumType) {
        this.days = days;
        this.amount = amount;
        this.premiumType = premiumType;
    }

    public static UserPremiumPeriod fromDays(Long days, String type) {
        return Arrays.stream(UserPremiumPeriod.values())
                .filter(userPremiumPeriod ->
                        userPremiumPeriod.getDays() == days && Objects.equals(userPremiumPeriod.getPremiumType(), type))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Not found premium period for " + days + " days and premium type " + type));
    }
}