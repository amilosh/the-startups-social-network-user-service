package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventFilter {
    boolean isApplicable(EventFilterDto eventFilterDto);

    List<Event> apply(List<Event> events, EventFilterDto filterDto);
}
