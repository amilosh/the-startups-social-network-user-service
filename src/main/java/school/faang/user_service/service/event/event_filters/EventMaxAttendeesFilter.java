package school.faang.user_service.service.event.event_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventMaxAttendeesFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return true;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        int maxAttendees = filters.getMaxAttendees();
        return events.filter(event -> event.getMaxAttendees() < maxAttendees);
    }
}