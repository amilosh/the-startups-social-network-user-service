package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@Component
@AllArgsConstructor
public class EventServiceImplementation implements EventService {

    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventValidator> eventValidators;

    public EventDto create(EventDto eventDto) {
        validateEvent(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        List<Skill> skills = skillRepository.findSkillsByGoalId(eventDto.getId());
        User owner = userRepository.getReferenceById(eventDto.getOwnerId());
        event.setRelatedSkills(skills);
        event.setOwner(owner);

        eventRepository.save(event);

        return eventMapper.toDto(event);
    }

    public void validateEvent(EventDto eventDto) {
        for (EventValidator validator : eventValidators) {
            if (!validator.isValid(eventDto)) {
                throw new DataValidationException(validator.getMessage());
            }
        }
    }


}
