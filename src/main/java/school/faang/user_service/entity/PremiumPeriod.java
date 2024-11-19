package school.faang.user_service.entity;

import lombok.Getter;
import school.faang.user_service.dto.client.Currency;

@Getter
public enum PremiumPeriod {
    MONTH(30, 10, Currency.USD),
    THREE_MONTHS(90, 25, Currency.USD),
    YEAR(365, 80, Currency.USD);

    private final int days;
    private final double price;
    private final Currency currency;

    PremiumPeriod(int days, double price, Currency currency) {
        this.days = days;
        this.price = price;
        this.currency = currency;
    }

    public static PremiumPeriod fromDays(int days) {
        for (PremiumPeriod period : PremiumPeriod.values()) {
            if (period.getDays() == days) {
                return period;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid days: %s", days));
    }
}
