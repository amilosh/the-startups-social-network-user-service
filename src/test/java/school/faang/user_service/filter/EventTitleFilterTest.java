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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventTitleFilterTest {
    @InjectMocks
    private EventTitleFilter eventTitleFilter;

    @Test
    public void eventTitleNotNullTest() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .title("Test tittle")
                .build();

        boolean result = eventTitleFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void eventTitleIsNullTest() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .title(null)
                .build();

        boolean result = eventTitleFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void applyEvenTitleTest() {
        Event event1 = mock(Event.class);
        Event event2 = mock(Event.class);

        when(event1.getTitle()).thenReturn("Test Event");
        when(event2.getTitle()).thenReturn("Another Event");

        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .title("Test.*")
                .build();

        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> filteredEvents = eventTitleFilter.apply(events, eventFilterDto);
        List<Event> result = filteredEvents.toList();

        int expected = 1;
        int resulSize = result.size();
        Event eventGetFirs = result.get(0);

        assertEquals(expected, resulSize, "Expected one event to match the title filter");
        assertEquals(event1, eventGetFirs,"Expected the returned event to be event1");
    }
}
