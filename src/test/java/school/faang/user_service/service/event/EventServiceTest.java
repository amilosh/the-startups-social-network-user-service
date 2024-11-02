package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.event.EventRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;

    @Test
    void checkEventExistenceReturnTrue() {
        long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(true);

        boolean result = eventService.checkEventExistence(eventId);

        assertTrue(result);
    }

    @Test
    void checkEventExistenceReturnFalse() {
        long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        boolean result = eventService.checkEventExistence(eventId);

        assertFalse(result);
    }
}
