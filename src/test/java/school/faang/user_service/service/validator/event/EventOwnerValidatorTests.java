package school.faang.user_service.service.validator.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.validator.event.EventOwnerValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EventOwnerValidatorTests {
    private final EventOwnerValidator eventOwnerValidator = new EventOwnerValidator();
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setId(1L);
    }

    @Test
    public void testOwnerIdIsNull() {
        eventDto.setOwnerId(null);
        assertFalse(eventOwnerValidator.isValid(eventDto));
        assertEquals(eventOwnerValidator.getMessage(), "OwnerId cannot be null");
    }

    @Test
    public void testOwnerIdIsNotNull() {
        eventDto.setOwnerId(1L);
        assertTrue(eventOwnerValidator.isValid(eventDto));
    }
}
