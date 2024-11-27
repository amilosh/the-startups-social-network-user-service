package school.faang.user_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Year;

@AllArgsConstructor
@Getter
public enum PremiumPeriod {
    MONTH(30, 10),
    THREE_MONTHS(90, 25),
    YEAR(Year.now().length(), 80);

    private final int price;
    private final int days;

    public static PremiumPeriod fromDays(int days) {
        return switch (days) {
            case 30 -> MONTH;
            case 90 -> THREE_MONTHS;
            case 365, 366 -> YEAR;
            default -> throw new IllegalArgumentException(
                    "Unfortunately we don't have premium for that amount of days: " + days);
        };
    }


}