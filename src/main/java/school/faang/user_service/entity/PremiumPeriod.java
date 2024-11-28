package school.faang.user_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.exception.PremiumPeriodNotFoundException;

import java.math.BigDecimal;
import java.time.Year;

@AllArgsConstructor
@Getter
public enum PremiumPeriod {
    MONTH(BigDecimal.valueOf(10), 30),
    THREE_MONTHS(BigDecimal.valueOf(25), 90),
    YEAR(BigDecimal.valueOf(80), Year.now().length());

    private final BigDecimal price;
    private final int days;

    public static PremiumPeriod fromDays(int days) {
        return switch (days) {
            case 30 -> MONTH;
            case 90 -> THREE_MONTHS;
            case 365, 366 -> YEAR;
            default -> throw new PremiumPeriodNotFoundException(
                    "Unfortunately, we don't have premium for that amount of days: " + days);
        };
    }


}