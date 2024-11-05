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
public class EventDescriptionFilterTest {
    @InjectMocks
    EventDescriptionFilter eventDescriptionFilter;

    @Test
    public void EventDescriptionNotNullTest() {
        EventFilterDto eventFilterDto = EventFilterDto
                .builder()
                .description("Description")
                .build();

        boolean result = eventDescriptionFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void EventDescriptionNullTest() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .description(null)
                .build();

        boolean result = eventDescriptionFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void applyEventDescriptionTest() {
        EventFilterDto filter = EventFilterDto.builder()
                .description("test")
                .build();

        Event event1 = Event.builder()
                .description("This is a test event")
                .build();

        Event event2 = Event.builder()
                .description("This is another event")
                .build();

        Stream<Event> events = Stream.of(event1, event2);
        List<Event> resultList = eventDescriptionFilter.apply(events, filter).toList();

        int expected = 1;
        int resultListSize = resultList.size();
        Event resultListIndex = resultList.get(0);

        assertEquals(expected, resultListSize);
        assertEquals(event1, resultListIndex);
    }
}
