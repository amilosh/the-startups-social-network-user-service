package school.faang.user_service.service.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Component
public class EventUserIdFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getUserId() != null;
    }

    @Override
    public List<Event> apply(List<Event> events, EventFilterDto filterDto) {
        events = events.stream()
                .filter(event -> event.getOwner().getId().equals(filterDto.getUserId()))
                .toList();

        return events;
    }
}
