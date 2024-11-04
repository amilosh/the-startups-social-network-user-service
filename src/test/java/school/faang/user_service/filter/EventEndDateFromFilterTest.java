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
public class EventEndDateFromFilterTest {
    @InjectMocks
    EventEndDateFromFilter eventEndDateFromFilter;

    @Test
    public void testEventDateFromNotNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .endDateFrom(LocalDateTime.now())
                .build();

        boolean result = eventEndDateFromFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void testEventDateFromNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .endDateFrom(null)
                .build();

        boolean result = eventEndDateFromFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void applyEventEndDateFromTest() {
        LocalDateTime filterEndDateFrom = LocalDateTime.of(2024, 11, 5, 0, 0);
        EventFilterDto filter = EventFilterDto.builder()
                .endDateFrom(filterEndDateFrom)
                .build();

        Event event1 = Event.builder()
                .endDate(LocalDateTime.of(2024, 11, 4, 0, 0))
                .build();

        Event event2 = Event.builder()
                .endDate(LocalDateTime.of(2024, 11, 6, 0, 0))
                .build();

        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> resultStream = eventEndDateFromFilter.apply(events, filter);
        List<Event> resultList = resultStream.toList();

        int expected = 1;
        int resultListSize = resultList.size();
        Event resultListIndex = resultList.get(0);

        assertEquals(expected, resultListSize);
        assertEquals(event1, resultListIndex);
    }
}
