package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
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
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        User eventOwner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        validateOwnerSkills(eventOwner, eventDto);
        Event event = eventMapper.toEntity(eventDto);
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    private void validateOwnerSkills(User eventOwner, EventDto eventDto) {
        Set<Long> ownerSkillIds = eventOwner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
        boolean hasAllRequiredSkills = eventDto.getRelatedSkills().stream()
                .allMatch(skill -> ownerSkillIds.contains(skill.getId()));
        if (!hasAllRequiredSkills) {
            throw new DataValidationException("Event owner does not have all the required skills for this event.");
        }
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        return eventMapper.toDto(events.toList());
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        User eventOwner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        validateOwnerSkills(eventOwner, eventDto);
        Event event = eventMapper.toEntity(eventDto);
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return eventMapper.toDto(events);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return eventMapper.toDto(events);
    }
}