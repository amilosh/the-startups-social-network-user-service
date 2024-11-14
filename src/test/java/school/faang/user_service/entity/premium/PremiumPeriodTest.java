package school.faang.user_service.entity.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.premium.PremiumNotFoundException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.exception.premium.PremiumErrorMessage.INVALID_PREMIUM_PERIOD;

class PremiumPeriodTest {

    @Test
    @DisplayName("Check all values from days")
    void testFromDaysCheckAllValues() {
        Arrays.stream(PremiumPeriod.values())
                .forEach(period -> assertThat(PremiumPeriod.fromDays(period.getDays())).isEqualTo(period));
    }

    @Test
    @DisplayName("Given wrong days when fromDays then throw exception")
    void testFromDaysWrongDaysExceptions() {
        Arrays.stream(PremiumPeriod.values())
                .forEach(this::assertPeriodException);
    }

    private void assertPeriodException(PremiumPeriod period) {
        assertThatThrownBy(() -> PremiumPeriod.fromDays(period.getDays() + 1))
                .isInstanceOf(PremiumNotFoundException.class)
                .hasMessageContaining(INVALID_PREMIUM_PERIOD, period.getDays() + 1,
                        PremiumPeriod.daysOptions());
    }
}
