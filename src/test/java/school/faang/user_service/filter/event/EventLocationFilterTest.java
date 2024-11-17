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

class EventLocationFilterTest {

    private EventLocationFilter eventLocationFilter;

    @BeforeEach
    void setUp() {
        eventLocationFilter = new EventLocationFilter();
    }

    @Test
    void testIsApplicableWithLocationReturnsTrue() {
        EventFilterDto filters = EventFilterDto.builder().location("New York").build();
        assertTrue(eventLocationFilter.isApplicable(filters));
    }

    @Test
    void testIsApplicableWithoutLocationReturnsFalse() {
        EventFilterDto filters = EventFilterDto.builder().build();
        assertFalse(eventLocationFilter.isApplicable(filters));
    }

    @Test
    void testApplyWithMatchingLocationReturnsFilteredEvents() {
        Event event1 = mock(Event.class);
        when(event1.isSameLocation("New York")).thenReturn(true);

        Event event2 = mock(Event.class);
        when(event2.isSameLocation("New York")).thenReturn(false);

        List<Event> events = List.of(event1, event2);
        Stream<Event> filteredEvents = eventLocationFilter.apply(events.stream(),
                EventFilterDto.builder().
                        location("New York")
                        .build());

        List<Event> filteredEventsList = filteredEvents.toList();

        assertEquals(1, filteredEventsList.size());
        assertTrue(filteredEventsList.contains(event1));
    }

    @Test
    void testApplyWithNoMatchingLocationReturnsEmpty() {
        Event event1 = mock(Event.class);
        when(event1.isSameLocation("Los Angeles")).thenReturn(false);

        List<Event> events = List.of(event1);
        Stream<Event> filteredEvents = eventLocationFilter.apply(events.stream(),
                EventFilterDto.builder()
                        .location("New York")
                        .build());

        assertEquals(0, filteredEvents.count());
    }

}