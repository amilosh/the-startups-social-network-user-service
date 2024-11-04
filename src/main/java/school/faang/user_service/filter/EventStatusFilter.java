package school.faang.user_service.filter;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public enum EventStatusFilter implements EventFilter {
    STATUS;

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getStatus() == filter.getStatus());
    }
}
