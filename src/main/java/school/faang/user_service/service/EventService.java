package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
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
        event.setRelatedSkills(eventDto.relatedSkills().stream()
                .map(relatedSkill -> skillMapper.toEntity(relatedSkill))
                .toList());

        Event savedEvent = eventRepository.save(event);
        log.info("Event saved with ID: {}", savedEvent.getId());

        EventDto newEventDto = eventMapper.toDto(event);

        EventDto updatedEventDto = EventDto.builder()
                .id(newEventDto.id())
                .title(newEventDto.title())
                .startDate(newEventDto.startDate())
                .endDate(newEventDto.endDate())
                .ownerId(newEventDto.ownerId())
                .description(newEventDto.description())
                .relatedSkills(savedEvent.getRelatedSkills().stream()
                        .map(skill -> skillMapper.toDto(skill))
                        .toList())
                .location(newEventDto.location())
                .maxAttendees(newEventDto.maxAttendees())
                .build();

        return updatedEventDto;
    }

    private void validateUserSkillsForEvent(User userOwner, EventDto eventDto) {
        List<Skill> relatedSkills = eventDto.relatedSkills().stream()
                .map(skillDto -> skillMapper.toEntity(skillDto))
                .toList();

        log.debug("Checking if user has required skills: {}", relatedSkills);

        if (!userHasSkills(userOwner, relatedSkills)) {
            log.error("User {} doesn't have required skills to create event", userOwner.getId());
            throw new DataValidationException("User don't have required skills to create event");
        }

        log.info("User {} has all required skills to create event", userOwner.getId());
    }

    private boolean userHasSkills(User userOwner, List<Skill> relatedSkills) {
        List<Skill> userOwnerSkills = userOwner.getSkills();

        return relatedSkills.stream()
                .allMatch(relatedSkill -> userOwnerSkills.stream()
                        .anyMatch(userskill -> userskill.getId() == relatedSkill.getId())
                );
    }
}
