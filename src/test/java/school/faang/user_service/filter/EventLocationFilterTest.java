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
public class EventLocationFilterTest {
    @InjectMocks
    EventLocationFilter eventLocationFilter;

    @Test
    public void testEventLocationNotNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .location("Test Location")
                .build();

        boolean result = eventLocationFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void testEventLocationIsNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .location(null)
                .build();

        boolean result = eventLocationFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void applyEventLocationTest() {
        EventFilterDto filter = EventFilterDto.builder()
                .location("Test Location")
                .build();

        Event event1 = Event.builder()
                .location("Test Location event1")
                .build();

        Event event2 = Event.builder()
                .location("Test Location event2")
                .build();

        Stream<Event> events = Stream.of(event1, event2);
        List<Event> resultList = eventLocationFilter.apply(events, filter).toList();

        int expected = 2;
        int resultListSize = resultList.size();

        assertEquals(expected, resultListSize);
        assertTrue(resultList.contains(event1));
        assertTrue(resultList.contains(event2));
    }
}
