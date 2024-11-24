package school.faang.user_service.entity.premium;

import lombok.Getter;
import school.faang.user_service.dto.client.Currency;

@Getter
public enum PremiumPeriod {
    MONTH(30, 10, Currency.USD, 100),
    THREE_MONTHS(90, 25, Currency.USD, 110),
    YEAR(365, 80, Currency.USD, 130);

    private final int days;
    private final double price;
    private final Currency currency;
    private final int searchScore;

    PremiumPeriod(int days, double price, Currency currency, int searchScore) {
        this.days = days;
        this.price = price;
        this.currency = currency;
        this.searchScore = searchScore;
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
