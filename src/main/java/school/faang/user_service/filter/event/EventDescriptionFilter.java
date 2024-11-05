package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventDescriptionFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getDescriptionPattern() != null && !filter.getDescriptionPattern().isBlank();
    }

    @Override
    public boolean apply(Event event, EventFilterDto filter) {
        if (event.getDescription() != null) {
            return event.getDescription().contains(filter.getDescriptionPattern());
        } else {
            return false;
        }
    }
}
