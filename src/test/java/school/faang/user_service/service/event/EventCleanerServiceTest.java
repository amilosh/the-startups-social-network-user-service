package school.faang.user_service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventCleanerServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventCleanerService eventCleanerService;

    @Test
    @DisplayName("Test deleteSelectedListEventsAsync")
    void testDeleteSelectedListEventsAsync() {
        List<Event> eventList = List.of(
                Event.builder().
                        id(1L).
                        build(),
                Event.builder().
                        id(2L).
                        build());
        eventCleanerService.deleteSelectedListEventsAsync(eventList);
        verify(eventRepository, times(1)).deleteById(eventList.get(0).getId());
        verify(eventRepository, times(1)).deleteById(eventList.get(1).getId());
    }
}
