package school.faang.user_service.entity.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.payment.Currency;
import school.faang.user_service.exception.premium.PremiumNotFoundException;

import java.util.Arrays;
import java.util.List;

import static school.faang.user_service.entity.premium.PremiumPeriodValues.COST_MONTH;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.COST_THREE_MOTH;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.COST_YEAR;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.DAYS_MONTH;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.DAYS_THREE_MOTH;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.DAYS_YEAR;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.PREMIUM_CURRENCY;
import static school.faang.user_service.exception.premium.PremiumErrorMessage.INVALID_PREMIUM_PERIOD;

@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {

    ONE_MONTH(DAYS_MONTH, COST_MONTH, PREMIUM_CURRENCY),
    THREE_MONTHS(DAYS_THREE_MOTH, COST_THREE_MOTH, PREMIUM_CURRENCY),
    ONE_YEAR(DAYS_YEAR, COST_YEAR, PREMIUM_CURRENCY);

    private final int days;
    private final double cost;
    private final Currency currency;

    public static PremiumPeriod fromDays(int days) {
        return switch (days) {
            case DAYS_MONTH -> ONE_MONTH;
            case DAYS_THREE_MOTH -> THREE_MONTHS;
            case DAYS_YEAR -> ONE_YEAR;
            default -> throw new PremiumNotFoundException(INVALID_PREMIUM_PERIOD, days, PremiumPeriod.daysOptions());
        };
    }

    public static List<Integer> daysOptions() {
        return Arrays
                .stream(PremiumPeriod.values())
                .map(PremiumPeriod::getDays)
                .toList();
    }
}
