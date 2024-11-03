package school.faang.user_service.service.event.event_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Component
public class EventLocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getLocation() != null;
    }

    @Override
    public List<Event> apply(List<Event> events, EventFilterDto filters) {
        return events.stream().filter(event -> event.getLocation().matches(filters.getLocation())).toList();
    }
}