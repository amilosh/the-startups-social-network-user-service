package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventTittleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.title() != null;
    }

    @Override
    public void apply(Stream<Event> allEvents, EventFilterDto filters) {
       allEvents.filter(event -> event.getTitle().equals(filters.title()));
    }
}

