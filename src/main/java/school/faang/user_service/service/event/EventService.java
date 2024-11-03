package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.event.EventValidation;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventValidation eventValidation;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final EventFilter eventFilter;

    public EventDto create(EventDto eventDto) {
        eventValidation.validateEventDto(eventDto, userService);
        Event event = eventRepository.save(eventMapper.dtoToEvent(eventDto));
        return eventMapper.eventToDto(event);
    }

    private Event findEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new DataValidationException("Event id not found"));
    }

    public EventDto getEvent(long eventId) {
        Event findEvent = findEventById(eventId);
        return eventMapper.eventToDto(findEvent);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();
        List<Event> filteredEvents = eventFilter.filterEvents(events, filter);
        return filteredEvents.stream()
                .map(eventMapper::eventToDto)
                .collect(Collectors.toList());
    }

    public void deleteEvent(long evenId) {
        eventRepository.deleteById(evenId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event event = findEventById(eventDto.getId());
        eventValidation.validateEventDto(eventDto, userService);
        Event updatedEvent = eventMapper.dtoToEvent(eventDto);
        updatedEvent.setId(event.getId());
        Event savedEvent = eventRepository.save(updatedEvent);
        return eventMapper.eventToDto(savedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
       List<Event> events =  eventRepository.findAllByUserId(userId);
       return eventMapper.toDtoList(events);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toDtoList(participatedEvents);
    }
}
