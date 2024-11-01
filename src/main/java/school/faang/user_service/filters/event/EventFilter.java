package school.faang.user_service.filters.event;

import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;


public interface EventFilter {
    public boolean isApplicable(EventFilterDto filters);

    public void apply(Stream<Event> allEvents, EventFilterDto filters);
}