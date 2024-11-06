package school.faang.user_service.validator.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventServiceValidator {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Event validateEventId(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Event with id: {} has not been found", eventId);
            return new EntityNotFoundException("Event with id: " + eventId + " was not found");
        });
    }

    public User validateUserId(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id: {} has not been found", userId);
            return new EntityNotFoundException("UserId " + userId + " was not found");
        });
    }

    public void validateOwnerSkills(User eventOwner, EventDto eventDto) {
        Set<Long> ownerSkillIds = eventOwner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
        boolean hasAllRequiredSkills = eventDto.getRelatedSkills().stream()
                .allMatch(skill -> ownerSkillIds.contains(skill.getId()));
        if (!hasAllRequiredSkills) {
            log.warn("Exception occurred in validateOwnerSkills method. Event owner doesn't have required skills");
            throw new DataValidationException("Event owner does not have all the required skills for this event.");
        }
    }
}