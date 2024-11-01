package school.faang.user_service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventLocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.location() != null;
    }

    @Override
    public void apply(Stream<Event> allEvents, EventFilterDto filters) {
        allEvents.filter(event -> event.getLocation().equals(filters.location()));
    }
}
