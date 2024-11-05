package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.event.EventValidation;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventValidation eventValidation;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        eventValidation.validateEvent(eventDto);
        Event event = eventRepository.save(eventMapper.dtoToEvent(eventDto));
        return eventMapper.eventToDto(event);
    }

    public EventDto getEventDto(long eventId) {
        Event findEvent = findEventById(eventId);
        return eventMapper.eventToDto(findEvent);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        return eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(events, ((eventStream, filter) -> filter.apply(eventStream, filters)), (e1, e2) -> e1)
                .map(eventMapper::eventToDto)
                .toList();
    }

    public void deleteEvent(long evenId) {
        eventRepository.deleteById(evenId);
    }

    public EventDto updateEvent(@NotNull EventDto eventDto) {
        Event event = findEventById(eventDto.getId());
        List<Long> skillsId = userService.findById(eventDto.getOwnerId()).getSkills().stream()
                .map(Skill::getId)
                .toList();

        eventValidation.validateRelatedSkills(eventDto, skillsId);
        Event updatedEvent = eventMapper.dtoToEventWithId(eventDto, event.getId());
        Event savedEvent = eventRepository.save(updatedEvent);
        return eventMapper.eventToDto(savedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return eventMapper.toDtoList(events);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toDtoList(participatedEvents);
    }
    
    private Event findEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event id not found"));
    }

    public boolean checkEventExistence(long eventId) {
        return eventRepository.existsById(eventId);
    }

}
