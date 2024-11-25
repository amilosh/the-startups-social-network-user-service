package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getTitlePattern() != null && !filter.getTitlePattern().isBlank();
    }

    @Override
    public boolean apply(Event event, EventFilterDto filter) {
        if (event.getTitle() != null) {
            return event.getTitle().equals(filter.getTitlePattern());
        } else {
            return false;
        }
    }
}
