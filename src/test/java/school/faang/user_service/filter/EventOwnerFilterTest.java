package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventOwnerFilterTest {
    private final EventOwnerIdFilter eventOwnerIdFilter = new EventOwnerIdFilter();

    @Test
    public void testEventOwnerIdNotNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .ownerId(1L)
                .build();

        boolean result = eventOwnerIdFilter.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void testEventOwnerIdIsNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .ownerId(null)
                .build();

        boolean result = eventOwnerIdFilter.isApplicable(eventFilterDto);
        assertFalse(result);
    }


    @Test
    public void applyOwnerIdFilterTest() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .ownerId(1L)
                .build();

        User user1 = new User();
                user1.setId(1L);

        Event event1 = Event.builder()
                .owner(user1)
                .build();

        User user2 = new User();
        user2.setId(2L);

        Event event2 = Event.builder()
                .owner(user2)
                .build();

        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> filteredEvents = eventOwnerIdFilter.apply(events, eventFilterDto);

        List<Event> resultList = filteredEvents.toList();

        int expected = 1;
        int resultSize = resultList.size();
        Event eventGetFirst = resultList.get(0);

        assertEquals(expected,resultSize);
        assertEquals(event1, eventGetFirst);
    }
}
