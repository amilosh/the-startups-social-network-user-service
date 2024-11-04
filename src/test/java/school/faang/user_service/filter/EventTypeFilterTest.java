package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventTypeFilterTest {

    private final EventTypeFilter eventTypeFilter = EventTypeFilter.TYPE;

    @Test
    public void eventTypeNotNullTest() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .eventType(EventType.WEBINAR)
                .build();

        boolean result = eventTypeFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void eventTypeIsNullTest() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .eventType(null)
                .build();

        boolean result = eventTypeFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void applyEventTypeTest() {
        Event event1 = mock(Event.class);
        Event event2 = mock(Event.class);

        when(event1.getType()).thenReturn(EventType.WEBINAR);
        when(event2.getType()).thenReturn(EventType.MEETING);

        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .eventType(EventType.WEBINAR)
                .build();

        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> filteredEvents = eventTypeFilter.apply(events, eventFilterDto);
        List<Event> result = filteredEvents.toList();

        int expected = 1;
        int resultSize = result.size();
        Event eventGetFirst = result.get(0);

        assertEquals(expected, resultSize, "Expected one event to match the type filter");
        assertEquals(event1, eventGetFirst, "Expected the returned event to be event1");
    }

    @Test
    public void applyEventTypeNoMatchTest() {
        Event event1 = mock(Event.class);
        Event event2 = mock(Event.class);

        when(event1.getType()).thenReturn(EventType.MEETING);
        when(event2.getType()).thenReturn(EventType.GIVEAWAY);

        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .eventType(EventType.WEBINAR)
                .build();

        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> filteredEvents = eventTypeFilter.apply(events, eventFilterDto);
        List<Event> result = filteredEvents.toList();

        assertTrue(result.isEmpty(), "Expected no events to match the type filter");
    }
}
