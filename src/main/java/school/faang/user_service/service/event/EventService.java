package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;

public interface EventService {
    public EventDto create(EventDto eventDto);
    public EventDto getEvent(Long id);
    public List<EventDto> getEventsByFilter(EventFilterDto filter);
    public List<EventDto> getOwnedEvents(long userId);
    public List<EventDto> getParticipatedEvents(long userId);
}
