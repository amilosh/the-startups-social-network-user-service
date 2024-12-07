package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filter);

    boolean apply(Event event, EventFilterDto filter);
}
