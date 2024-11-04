package school.faang.user_service.filter;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public enum EventTypeFilter implements EventFilter {
    TYPE;

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getEventType() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getType() == filter.getEventType());
    }
}
