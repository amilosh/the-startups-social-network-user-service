package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.event_filters.EventFilter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        User eventOwner = validateUserId(eventDto.getOwnerId());
        validateOwnerSkills(eventOwner, eventDto);
        Event event = eventMapper.toEntity(eventDto);
        event = eventRepository.save(event);
        log.info("New event saved to the database;");
        return eventMapper.toDto(event);
    }

    public EventDto get(long eventId) {
        Event event = validateEventId(eventId);
        log.info("Event with " + eventId + " has been retrieved from the database");
        return eventMapper.toDto(event);
    }

    public List<EventDto> getByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        log.info("A filtered list of events has been retrieved. Filters are: " + filters);
        return eventMapper.toDto(events.toList());
    }

    public void delete(long eventId) {
        eventRepository.deleteById(eventId);
        log.info("An event with id " + eventId + " has been deleted from the database.");
    }

    public EventDto update(EventDto eventDto) {
        Event eventToUpdate = validateEventId(eventDto.getId());
        User eventOwner = validateUserId(eventDto.getOwnerId());
        validateOwnerSkills(eventOwner, eventDto);
        eventMapper.update(eventToUpdate, eventDto);
        log.info("An event with id " + eventToUpdate.getId() + " has been updated.");
        eventToUpdate = eventRepository.save(eventToUpdate);
        log.info("An updated event with id " + eventToUpdate.getId() + " has been saved to the database");
        return eventMapper.toDto(eventToUpdate);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        log.info("A list of events owned by user with id " + userId + " had been retrieved");
        return eventMapper.toDto(events);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        log.info("A list of events where user with id " + userId + " participates has been retrieved");
        return eventMapper.toDto(events);
    }

    private Event validateEventId(long eventId) {
        log.warn("Event with id: " + eventId + " has not been found");
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    private User validateUserId(long userId) {
        log.warn("User with id: " + userId + " has not been found");
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserId is not found"));
    }

    private void validateOwnerSkills(User eventOwner, EventDto eventDto) {
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