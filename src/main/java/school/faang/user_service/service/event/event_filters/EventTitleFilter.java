package school.faang.user_service.service.event.event_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Component
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getTitlePattern() != null && !filters.getTitlePattern().isEmpty();
    }

    @Override
    public List<Event> apply(List<Event> events, EventFilterDto filters) {
        String titlePattern = filters.getTitlePattern();
        return events.stream().filter(event -> event.getTitle() != null && event.getTitle().toLowerCase().contains(titlePattern.toLowerCase())).toList();
    }
}