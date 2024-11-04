package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EventStartDateFromTest {
    @InjectMocks
    EventStartDateFromFilter startDateFromFilter;

    @Test
    public void testEventStartDateFromNotNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .startDateFrom(LocalDateTime.now())
                .build();

        boolean result = startDateFromFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void testEventStartDateFromIsNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .startDateFrom(null)
                .build();

        boolean result = startDateFromFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void applyEventStartDateFromFilterTest() {
        LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 31, 0, 0);

        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .startDateFrom(localDateTime)
                .build();

        Event event1 = Event.builder()
                .startDate(localDateTime2)
                .build();

        Event event2 = Event.builder()
                .startDate(localDateTime)
                .build();

        Event event3 = Event.builder()
                .startDate(localDateTime)
                .build();

        Stream<Event> events = Stream.of(event1, event2, event3);

        Stream<Event> filteredEvents = startDateFromFilter.apply(events, eventFilterDto);

        List<Event> resultList = filteredEvents.toList();

        assertEquals(1, resultList.size());
        assertEquals(event1, resultList.get(0));
    }
}
