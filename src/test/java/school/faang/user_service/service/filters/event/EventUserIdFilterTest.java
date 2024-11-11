package school.faang.user_service.service.filters.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filters.event.EventUserIdFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EventUserIdFilterTest {
    @Spy
    private EventUserIdFilter eventUserIdFilter;
    private Stream<Event> events;
    private EventFilterDto filterDto;

    @BeforeEach
    void setUp() {
        events = Stream.of(
                Event.builder().title("title1").owner(User.builder().id(1L).build()).build(),
                Event.builder().title("title2").owner(User.builder().id(2L).build()).build(),
                Event.builder().title("title3").owner(User.builder().id(1L).build()).build());

        filterDto = EventFilterDto.builder().userId(1L).build();
    }

    @Test
    public void eventEventTitleFilterTest() {
        List<Event> filteredEvents = eventUserIdFilter.apply(events, filterDto).toList();

        assertEquals(2, filteredEvents.size());
        assertEquals("title1", filteredEvents.get(0).getTitle());
        assertEquals("title3", filteredEvents.get(1).getTitle());
    }
}
