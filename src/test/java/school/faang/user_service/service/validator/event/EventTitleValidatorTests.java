package school.faang.user_service.service.validator.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.validator.event.EventTitleValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventTitleValidatorTests {
    private final EventTitleValidator eventTitleValidator = new EventTitleValidator();
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setId(1L);
    }

    @Test
    public void testTitleIsNull() {
        eventDto.setTitle(null);
        assertFalse(eventTitleValidator.isValid(eventDto));
        assertEquals(eventTitleValidator.getMessage(), "Title cannot be blank or null");
    }

    public void testTitleIsBlank() {
        eventDto.setTitle(" ");
        assertFalse(eventTitleValidator.isValid(eventDto));
        assertEquals(eventTitleValidator.getMessage(), "Title cannot be blank or null");
    }

    @Test
    public void testTitleIsNotNull() {
        eventDto.setTitle("title");
        assertTrue(eventTitleValidator.isValid(eventDto));
    }
}
