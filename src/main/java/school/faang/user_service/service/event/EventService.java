package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final EventDtoValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        eventValidator.validate(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userService.findById(eventDto.getOwnerId()));
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }
}
