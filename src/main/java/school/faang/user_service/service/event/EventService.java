package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;

public interface EventService {
    public EventDto create(EventDto eventDto);
    public EventDto getEvent(Long id);
}
