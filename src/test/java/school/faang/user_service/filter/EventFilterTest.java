package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.event.Rating;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EventFilterTest {
    @InjectMocks
    private EventFilter eventFilter;

    private void assertFilteredEvents(EventFilterDto filter, List<Event> events, Event... expectedEvents) {
        List<Event> filteredEvents = eventFilter.filterEvents(events, filter);

        assertEquals(expectedEvents.length, filteredEvents.size());

        Arrays.stream(expectedEvents).forEach(expectedEvent ->
                assertTrue(filteredEvents.contains(expectedEvent),
                        "Expected event not found in filtered events: " + expectedEvent));
    }


    @Test
    public void testFilterEventsById() {
        Event event1 = new Event();
        event1.setId(1L);

        Event event2 = new Event();
        event2.setId(2L);

        Event event3 = new Event();
        event3.setId(3L);

        List<Event> events = Arrays.asList(event1, event2, event3);

        EventFilterDto filter = EventFilterDto.builder()
                .id(2L)
                .build();

        assertFilteredEvents(filter, events, event2);
    }

    @Test
    public void testFilterEventsByTitle() {
        Event event1 = new Event();
        event1.setTitle("Event1");

        Event event2 = new Event();
        event2.setTitle("Event2");

        List<Event> events = Arrays.asList(event1, event2);

        EventFilterDto filter = EventFilterDto.builder()
                .title("Event1")
                .build();

        assertFilteredEvents(filter, events, event1);
    }

    @Test
    public void testFilterEventsByDescription() {
        Event event1 = new Event();
        event1.setDescription("Description1");

        Event event2 = new Event();
        event2.setDescription("Description2");

        List<Event> events = Arrays.asList(event1, event2);

        EventFilterDto filter = EventFilterDto.builder()
                .description("Description1")
                .build();

        assertFilteredEvents(filter, events, event1);
    }

    @Test
    public void testFilterEventsByEventType() {
        Event event1 = new Event();
        event1.setType(EventType.MEETING);

        Event event2 = new Event();
        event2.setType(EventType.PRESENTATION);

        List<Event> events = Arrays.asList(event1, event2);

        EventFilterDto filter = EventFilterDto.builder()
                .eventType(EventType.MEETING)
                .build();

        assertFilteredEvents(filter, events, event1);
    }

    @Test
    public void testFilterEventsByOwnerId() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);


        Event event1 = new Event();
        event1.setOwner(user1);

        Event event2 = new Event();
        event2.setOwner(user2);

        List<Event> events = Arrays.asList(event1, event2);

        EventFilterDto filter = EventFilterDto.builder()
                .ownerId(1L)
                .build();

        assertFilteredEvents(filter, events, event1);
    }

    @Test
    public void testFilterEventsBySkillsIds() {
        Skill skill1 = new Skill();
        skill1.setId(1L);

        Skill skill2 = new Skill();
        skill2.setId(2L);

        Event event1 = new Event();
        event1.setRelatedSkills(Arrays.asList(skill1, skill2));

        Event event2 = new Event();
        event2.setRelatedSkills(List.of(skill2));

        List<Event> events = Arrays.asList(event1, event2);

        EventFilterDto filter = EventFilterDto.builder()
                .skillIds(List.of(skill1.getId()))
                .build();

        assertFilteredEvents(filter, events, event1);

        filter = EventFilterDto.builder()
                .skillIds(null)
                .build();

        assertFilteredEvents(filter, events, event1, event2);
    }

    @Test
    public void testFilterEventsByGetStartDateFrom() {
        LocalDateTime now = LocalDateTime.now();

        Event event1 = new Event();
        event1.setStartDate(now);

        Event event2 = new Event();
        event2.setStartDate(now);

        List<Event> events = Arrays.asList(event1, event2);
        EventFilterDto filter = EventFilterDto.builder()
                .startDateFrom(now)
                .build();

        assertFilteredEvents(filter, events, event1, event2);
    }

    @Test
    public void testFilterEventsByGetEndDateFrom() {
        LocalDateTime now = LocalDateTime.now();

        Event event1 = new Event();
        event1.setEndDate(now.plusHours(1));

        Event event2 = new Event();
        event2.setEndDate(now.plusHours(2));

        List<Event> events = Arrays.asList(event1, event2);
        EventFilterDto filter = EventFilterDto.builder()
                .endDateFrom(now)
                .build();

        assertFilteredEvents(filter, events, event1, event2);
    }

    @Test
    public void testFilterEventsByEndDateFrom() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusHours(1);

        Event event1 = new Event();
        event1.setEndDate(later);

        Event event2 = new Event();
        event2.setEndDate(later.plusHours(1));

        List<Event> events = Arrays.asList(event1, event2);
        EventFilterDto filter = EventFilterDto.builder()
                .endDateFrom(now)
                .build();

        assertFilteredEvents(filter, events, event1, event2); // ќжидаем оба событи€
    }

    @Test
    public void testFilterEventsByLocation() {
        Event event1 = new Event();
        event1.setLocation("Location1");

        Event event2 = new Event();
        event2.setLocation("Location2");

        List<Event> events = Arrays.asList(event1, event2);
        EventFilterDto filter = EventFilterDto.builder()
                .location("Location1")
                .build();

        assertFilteredEvents(filter, events, event1);
    }

    @Test
    public void testFilterEventsByMaxAttendees() {
        Event event1 = new Event();
        event1.setMaxAttendees(10);

        Event event2 = new Event();
        event2.setMaxAttendees(20);

        List<Event> events = Arrays.asList(event1, event2);
        EventFilterDto filter = EventFilterDto.builder()
                .maxAttendees(15)
                .build();

        assertFilteredEvents(filter, events, event1);
    }

    @Test
    public void testFilterEventsByMinRating() {
        Rating rating1 = new Rating();
        rating1.setRate(4);

        Rating rating2 = new Rating();
        rating2.setRate(5);

        Event event1 = new Event();
        event1.setRatings(List.of(rating1));

        Event event2 = new Event();
        event2.setRatings(List.of(rating2));

        List<Event> events = Arrays.asList(event1, event2);
        EventFilterDto filter = EventFilterDto.builder()
                .minRating(4.5)
                .build();

        assertFilteredEvents(filter, events, event2);
    }

    @Test
    public void testFilterEventsByStatus() {
        Event event1 = new Event();
        event1.setStatus(EventStatus.COMPLETED);

        Event event2 = new Event();
        event2.setStatus(EventStatus.IN_PROGRESS);

        List<Event> events = Arrays.asList(event1, event2);
        EventFilterDto filter = EventFilterDto.builder()
                .status(EventStatus.COMPLETED)
                .build();

        assertFilteredEvents(filter, events, event1);
    }
}
