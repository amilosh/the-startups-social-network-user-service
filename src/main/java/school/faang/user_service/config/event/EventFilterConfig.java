package school.faang.user_service.config.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.filters.event.EventTitleFilter;
import school.faang.user_service.filters.event.EventUserIdFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class EventFilterConfig {
    private final EventService eventService;

    @Bean(value = "eventFilters")
    public List<EventFilter> getEventFilters(EventTitleFilter eventTitleFilter, EventUserIdFilter eventUserIdFilter) {
        List<EventFilter> filters = new ArrayList<>();
        filters.add(eventTitleFilter);
        filters.add(eventUserIdFilter);
        return filters;
    }
}
