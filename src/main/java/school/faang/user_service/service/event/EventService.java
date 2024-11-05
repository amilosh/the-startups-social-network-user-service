package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.event_filters.EventFilter;
import school.faang.user_service.validation.event.EventServiceValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final EventServiceValidator eventServiceValidator;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        User eventOwner = eventServiceValidator.validateUserId(eventDto.getOwnerId());
        eventServiceValidator.validateOwnerSkills(eventOwner, eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setId(null);
        event = eventRepository.save(event);
        log.info("New event saved to the database;");
        return eventMapper.toDto(event);
    }

    public EventDto get(long eventId) {
        Event event = eventServiceValidator.validateEventId(eventId);
        log.info("Event with {} has been retrieved from the database", eventId);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getByFilter(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();
        log.info(eventFilters.toString());
        for (EventFilter filter : eventFilters) {
            if (filter != null && filter.isApplicable(filters)) {
                events = filter.apply(events, filters);
            }
        }
        log.info("A filtered list of events has been retrieved. Filters are: {}", filters);
        return eventMapper.toDto(events);
    }

    public void delete(long eventId) {
        eventRepository.deleteById(eventId);
        log.info("An event with id {} has been deleted from the database.", eventId);
    }

    public EventDto update(EventDto eventDto) {
        Event eventToUpdate = eventServiceValidator.validateEventId(eventDto.getId());
        User eventOwner = eventServiceValidator.validateUserId(eventDto.getOwnerId());
        eventServiceValidator.validateOwnerSkills(eventOwner, eventDto);

        Event updatedEvent = eventMapper.toEntity(eventDto);
        Event savedEvent = eventRepository.save(updatedEvent);

        log.info("An updated event with id {} has been saved to the database", eventToUpdate.getId());
        return eventMapper.toDto(savedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        log.info("A list of events owned by user with id {} had been retrieved", userId);
        return eventMapper.toDto(events);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        log.info("A list of events where user with id {} participates has been retrieved", userId);
        return eventMapper.toDto(events);
    }
}