package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventOwnerFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getOwnerIdPattern() != null;
    }

    @Override
    public boolean apply(Event event, EventFilterDto filter) {
        if (event.getOwner() != null && event.getOwner().getId() != null) {
            return event.getOwner().getId().equals(filter.getOwnerIdPattern());
        } else {
            return false;
        }
    }
}