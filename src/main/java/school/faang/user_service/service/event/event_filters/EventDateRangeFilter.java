package school.faang.user_service.service.event.event_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventDateRangeFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getStartDate() != null && filters.getEndDate() != null;
    }

    @Override
    public List<Event> apply(List<Event> events, EventFilterDto filters) {
        LocalDateTime startDateFromFilter = filters.getStartDate();
        LocalDateTime endDateFromFilter = filters.getEndDate();
        return events.stream().filter(event -> event.getEndDate() != null &&
                (event.getEndDate().isEqual(startDateFromFilter) || event.getEndDate().isEqual(endDateFromFilter) ||
                        (event.getEndDate().isAfter(startDateFromFilter) && event.getEndDate().isBefore(endDateFromFilter)))).toList();
    }
}