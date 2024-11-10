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

class EventOwnerFilterTest {
    private EventOwnerFilter eventOwnerFilter;

    @BeforeEach
    void setUp() {
        eventOwnerFilter = new EventOwnerFilter();
    }

    @Test
    void testIsApplicable_WithOwnerId_ReturnsTrue() {
        EventFilterDto filters = EventFilterDto.builder().ownerId(1L).build();
        assertTrue(eventOwnerFilter.isApplicable(filters));
    }

    @Test
    void testIsApplicable_WithoutOwnerId_ReturnsFalse() {
        EventFilterDto filters = EventFilterDto.builder().build();
        assertFalse(eventOwnerFilter.isApplicable(filters));
    }

    @Test
    void testApply_WithMatchingOwnerId_ReturnsFilteredEvents() {
        Event event1 = mock(Event.class);
        when(event1.isSameOwnerById(1L)).thenReturn(true);

        Event event2 = mock(Event.class);
        when(event2.isSameOwnerById(1L)).thenReturn(false);

        List<Event> events = List.of(event1, event2);
        Stream<Event> filteredEvents = eventOwnerFilter.apply(events.stream(),
                EventFilterDto.builder()
                        .ownerId(1L)
                        .build());

        List<Event> filteredEventsList = filteredEvents.toList();

        assertEquals(1, filteredEventsList.size());
        assertTrue(filteredEventsList.contains(event1));
    }

    @Test
    void testApply_WithNoMatchingOwnerId_ReturnsEmpty() {
        Event event1 = mock(Event.class);
        when(event1.isSameOwnerById(2L)).thenReturn(false);

        List<Event> events = List.of(event1);
        Stream<Event> filteredEvents = eventOwnerFilter.apply(events.stream(), EventFilterDto.builder().ownerId(1L).build());

        assertEquals(0, filteredEvents.count());
    }

}