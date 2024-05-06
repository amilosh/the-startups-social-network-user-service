package school.faang.user_service.service.event.filter;

import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

 class EventStatusFilter implements EventFilter {
     @Override
     public boolean isApplicable(EventFilterDto filters) {
         return filters.getStatusPattern() != null && !filters.getStatusPattern().isBlank();
     }

     @Override
     public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
         return events
                 .filter(event -> event.getStatus().getMessage().contains(filters.getStatusPattern()));
     }
}
