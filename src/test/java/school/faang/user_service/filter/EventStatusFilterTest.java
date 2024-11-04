package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventStatusFilterTest {
    private EventStatusFilter filter;
    private EventFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = EventStatusFilter.STATUS;
        filterDto = EventFilterDto.builder().build();
    }

    @Test
    void testIsApplicableWithNullStatus() {
        filterDto.setStatus(null);
        assertFalse(filter.isApplicable(filterDto), "Expected isApplicable to return false when status is null");
    }

    @Test
    void testIsApplicableWithStatus() {
        filterDto.setStatus(EventStatus.PLANNED);
        assertTrue(filter.isApplicable(filterDto), "Expected isApplicable to return true when status is not null");
    }

    @Test
    void testApplyFiltersEventsByStatus() {
        Event event1 = mock(Event.class);
        Event event2 = mock(Event.class);
        when(event1.getStatus()).thenReturn(EventStatus.PLANNED);
        when(event2.getStatus()).thenReturn(EventStatus.IN_PROGRESS);

        filterDto.setStatus(EventStatus.PLANNED);
        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> filteredEvents = filter.apply(events, filterDto);
        List<Event> result = filteredEvents.toList();

        assertEquals(1, result.size(), "Expected one event to be returned");
        assertEquals(event1, result.get(0), "Expected the returned event to be event1");
    }
}
