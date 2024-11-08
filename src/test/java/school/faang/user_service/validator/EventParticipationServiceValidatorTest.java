package school.faang.user_service.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.validator.EventParticipationServiceValidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class EventParticipationServiceValidatorTest {

    @Test
    @DisplayName("Проверка на отрицательный UserId")
    void testValidateUserId_UserIdIsNegative() {
        long negativeUserId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                EventParticipationServiceValidator.validateUserId(negativeUserId)
        );
        assertEquals("userId cannot be negative: userId=-1", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка на отрицательный EventId")
    void testValidateEventId_EventIdIsNegative() {
        long negativeEventId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> EventParticipationServiceValidator.validateEventId(negativeEventId));

        assertEquals("eventId cannot be negative: eventId=-1", exception.getMessage());
    }
}
