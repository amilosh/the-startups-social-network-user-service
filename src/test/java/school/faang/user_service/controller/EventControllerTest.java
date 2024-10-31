package school.faang.user_service.aaa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.EventValidation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @Mock
    private EventValidation eventValidation;

    @Test
    public void testCreate() {
        EventDto eventDto = EventDto.builder().build();

        when(eventService.create(eventDto)).thenReturn(eventDto);
        EventDto result = eventController.create(eventDto);

        assertEquals(result,eventDto);
        verify(eventValidation, times(1)).validateEvent(eventDto);
    }
}
