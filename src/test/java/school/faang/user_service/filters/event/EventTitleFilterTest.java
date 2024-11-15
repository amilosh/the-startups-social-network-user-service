package school.faang.user_service.filters.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filters.event.EventTitleFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EventTitleFilterTest {
    @Spy
    private EventTitleFilter eventTitleFilter;
    private Stream<Event> events;
    private EventFilterDto filterDto;

    @BeforeEach
    void setUp() {
        events = Stream.of(
                Event.builder().title("title1").build(),
                Event.builder().title("title2").build(),
                Event.builder().title("title2").build());

        filterDto = EventFilterDto.builder().titlePattern("title1").build();
    }

    @Test
    public void eventEventTitleFilterTest() {
        List<Event> filteredEvents = eventTitleFilter.apply(events, filterDto).toList();

        assertEquals(1, filteredEvents.size());
        assertEquals("title1", filteredEvents.get(0).getTitle());
    }
}
