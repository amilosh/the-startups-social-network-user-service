package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EventMaxAttendeesFilterTest {
    @InjectMocks
    EventMaxAttendeesFilter eventMaxAttendeesFilter;

    @Test
    public void testEventMaxAttendeesNotNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .maxAttendees(20)
                .build();
        boolean result = eventMaxAttendeesFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void testEventMaxAttendeesIsNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .maxAttendees(null)
                .build();

        boolean result = eventMaxAttendeesFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void EventMaxAttendeesFilterTest() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .maxAttendees(20)
                .build();

        Event event1 = Event.builder()
                .maxAttendees(20)
                .build();

        Event event2 = Event.builder()
                .maxAttendees(30)
                .build();

        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> filteredEvents = eventMaxAttendeesFilter.apply(events, eventFilterDto);

        List<Event> resultList = filteredEvents.toList();

        assertEquals(1, resultList.size());
        assertEquals(event1, resultList.get(0));
    }
}
