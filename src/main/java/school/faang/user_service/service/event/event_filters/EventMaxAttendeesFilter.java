package school.faang.user_service.service.event.event_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Component
public class EventMaxAttendeesFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getMaxAttendees() != null;
    }

    @Override
    public List<Event> apply(List<Event> events, EventFilterDto filters) {
        int maxAttendees = filters.getMaxAttendees();
        return events.stream().filter(event -> event.getMaxAttendees() < maxAttendees).toList();
    }
}