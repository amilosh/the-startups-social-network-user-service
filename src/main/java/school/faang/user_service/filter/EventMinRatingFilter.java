package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;

import java.util.stream.Stream;

@Component
public class EventMinRatingFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getMinRating() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getRatings().stream().
                mapToDouble(Rating::getId)
                .average()
                .orElse(0.0) >= filter.getMinRating());
    }
}
