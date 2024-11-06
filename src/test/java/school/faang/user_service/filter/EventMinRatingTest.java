package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventMinRatingTest {
    private final EventMinRatingFilter eventMinRating = new EventMinRatingFilter();

    @Test
    public void testEventMinRatingFilterNotNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .minRating(20.0)
                .build();

        boolean result = eventMinRating.isApplicable(eventFilterDto);
        assertTrue(result);
    }

    @Test
    public void testEventMinRatingFilterIsNull() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .minRating(null)
                .build();

        boolean result = eventMinRating.isApplicable(eventFilterDto);
        assertFalse(result);
    }

    @Test
    public void applyEventMinRatingFilterTest() {
        EventFilterDto eventFilterDto = EventFilterDto.builder()
                .minRating(3.5)
                .build();

        Rating rating1 = Rating.builder()
                .id(4)
                .build();

        Rating rating2 = Rating.builder()
                .id(5)
                .build();

        Event event1 = Event.builder()
                .ratings(List.of(rating1, rating2))
                .build();

        Rating rating3 = Rating.builder()
                .id(2)
                .build();

        Rating rating4 = Rating.builder()
                .id(3)
                .build();

        Event event2 = Event.builder()
                .ratings(List.of(rating3, rating4))
                .build();

        Stream<Event> events = Stream.of(event1, event2);

        Stream<Event> filteredEvents = eventMinRating.apply(events, eventFilterDto);

        List<Event> resultList = filteredEvents.toList();

        int expected = 1;
        int resulSize = resultList.size();
        Event resultGetFirst = resultList.get(0);

        assertEquals(expected, resulSize);
        assertEquals(event1, resultGetFirst);
    }
}
