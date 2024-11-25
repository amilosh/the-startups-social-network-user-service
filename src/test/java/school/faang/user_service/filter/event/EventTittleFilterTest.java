package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventTittleFilterTest {
    private EventTittleFilter eventTittleFilter;

    @BeforeEach
    void setUp() {
        eventTittleFilter = new EventTittleFilter();
    }

    @Test
    void testIsApplicable_WithTitle_ReturnsTrue() {
        EventFilterDto filters = EventFilterDto.builder().title("Sample Event").build();
        assertTrue(eventTittleFilter.isApplicable(filters));
    }

    @Test
    void testIsApplicable_WithoutTitle_ReturnsFalse() {
        EventFilterDto filters = EventFilterDto.builder().build();
        assertFalse(eventTittleFilter.isApplicable(filters));
    }

    @Test
    void testApply_WithMatchingTitle_ReturnsFilteredEvents() {
        Event event1 = mock(Event.class);
        when(event1.isSameTitle("Sample Event")).thenReturn(true);

        Event event2 = mock(Event.class);
        when(event2.isSameTitle("Sample Event")).thenReturn(false);

        List<Event> events = List.of(event1, event2);
        Stream<Event> filteredEvents = eventTittleFilter.apply(events.stream(),
                EventFilterDto.builder()
                        .title("Sample Event")
                        .build());
        List<Event> filteredEventsList = filteredEvents.toList();

        assertEquals(1, filteredEventsList.size());
        assertTrue(filteredEventsList.contains(event1));
    }

    @Test
    void testApply_WithNoMatchingTitle_ReturnsEmpty() {
        Event event1 = mock(Event.class);
        when(event1.isSameTitle("Another Event")).thenReturn(false);

        List<Event> events = List.of(event1);
        Stream<Event> filteredEvents = eventTittleFilter.apply(events.stream(),
                EventFilterDto.builder().
                        title("Sample Event").build());

        assertEquals(0, filteredEvents.count());
    }

}