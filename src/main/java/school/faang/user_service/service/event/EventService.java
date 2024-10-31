package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.EventValidation;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final EventValidation eventValidation;

    public EventDto create(EventDto event) {
        eventValidation.validateEvent(event);

        User user = userRepository.findById(event.getOwnerId()).orElseThrow(() -> new NoSuchElementException("User not found"));
        List<Skill> userOwnerSkills = user.getSkills();

        Event findEvent = eventRepository.findById(event.getId()).orElseThrow(() -> new NoSuchElementException("Event not found"));
        List<Skill> eventSkills  = findEvent.getRelatedSkills();

        return getEventDto(event, userOwnerSkills, eventSkills);
    }

    private EventDto getEventDto(EventDto event, List<Skill> userOwnerSkills, List<Skill> eventSkills) {
        boolean check = userOwnerSkills.stream().allMatch(eventSkills::contains);

        if (!check) {
            throw new NoSuchElementException();
        } else  {
            Event eventEntity = eventMapper.dtoToEvent(event);
            Event savedEvent = eventRepository.save(eventEntity);
            return eventMapper.eventToDto(savedEvent);
        }
    }
}
