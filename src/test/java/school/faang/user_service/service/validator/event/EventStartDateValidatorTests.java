package school.faang.user_service.service.validator.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.validator.event.EventStartDateValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventStartDateValidatorTests {
    private final EventStartDateValidator eventStartDateValidator = new EventStartDateValidator();
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setId(1L);
    }

    @Test
    public void testStartDateIsNull() {
        eventDto.setStartDate(null);
        assertFalse(eventStartDateValidator.isValid(eventDto));
        assertEquals(eventStartDateValidator.getMessage(), "Start date cannot be null");
    }

    @Test
    public void testStartDateIsNotNull() {
        eventDto.setStartDate(LocalDateTime.of(2024, 07, 05, 0, 0));
        assertTrue(eventStartDateValidator.isValid(eventDto));
    }
}

