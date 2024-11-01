package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final SkillMapper skillMapper;

    public EventDto create(EventDto eventDto) {
        log.info("Processing creation of event with title: {}", eventDto.title());
        User userOwner = userService.getUserById(eventDto.ownerId());

        log.debug("Validating user skills for owner: {}", userOwner);
        validateUserSkillsForEvent(userOwner, eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userOwner);

        Event savedEvent = eventRepository.save(event);
        log.info("Event saved with ID: {}", savedEvent.getId());

        return eventMapper.toDto(event);
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataValidationException("Event do not found"));
        log.info("Event found by {}", eventId);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        eventRepository.findAll();
    }

    private void validateUserSkillsForEvent(User userOwner, EventDto eventDto) {
        List<Skill> relatedSkills = eventDto.relatedSkills().stream()
                .map(skillDto -> skillMapper.toEntity(skillDto))
                .toList();
        List<Skill> userOwnerSkills = userOwner.getSkills();

        log.debug("Checking if user has required skills: {}", relatedSkills);

        if (!userHasSkills(userOwnerSkills, relatedSkills)) {
            log.error("User {} doesn't have required skills to create event", userOwner.getId());
            throw new DataValidationException("User don't have required skills to create event");
        }

        log.info("User {} has all required skills to create event", userOwner.getId());
    }

    private boolean userHasSkills(List<Skill> userOwnerSkills, List<Skill> relatedSkills) {


        return relatedSkills.stream()
                .allMatch(relatedSkill -> userOwnerSkills.stream()
                        .anyMatch(userskill -> userskill.getId() == relatedSkill.getId())
                );
    }
}
