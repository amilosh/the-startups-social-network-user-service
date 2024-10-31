package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.Validator.EventValidator;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.event.EventRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventValidator validator;

    private final EventMapper eventMapper;
    private final SkillMapper skillMapper;

    public EventDto create(EventDto eventDto) {
        log.info("Processing creation of event with title: {}", eventDto.title());
        User userOwner = userService.getUserById(eventDto.ownerId());

        log.debug("Validating user skills for owner: {}", userOwner);
        validator.validateUserSkillsForEvent(userOwner, eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userOwner);
        event.setRelatedSkills(eventDto.relatedSkills().stream()
                .map(relatedSkill -> skillMapper.toEntity(relatedSkill))
                .toList());

        Event savedEvent = eventRepository.save(event);
        log.info("Event saved with ID: {}", savedEvent.getId());

        return eventMapper.toDto(event);
    }
}
