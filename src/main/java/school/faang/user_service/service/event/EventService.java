package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.EventValidation;
import school.faang.user_service.validator.UserValidator;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {

    private final EventValidation eventValidation;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final List<EventFilter> eventFilters;
    private final UserValidator userValidator;
    private final EventCleanerService eventCleanerService;

    @Value("${batch.size.eventBatch}")
    private int batchSize;

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

    public EventDto updateEvent(EventDto eventDto) {
        Event event = findEventById(eventDto.getId());
        List<Long> skillsId = userService.findUserById(eventDto.getOwnerId()).getSkills().stream()
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

    public List<Event> getEvents(long userId) {
        userValidator.validateUserById(userId);
        return eventRepository.findAllByUserId(userId);
    }

    @Transactional
    public void cancelUserOwnedEvents(long userId) {
        getEvents(userId).forEach(event -> {
            if (event.getStatus() == EventStatus.PLANNED || event.getStatus() == EventStatus.IN_PROGRESS) {
                event.setStatus(EventStatus.CANCELED);
            }
        });
    }

    public void deleteCompletedAndCanceledEvent() {
        List<Event> eventList = eventRepository.findAllCompletedAndCanceledEvents();
        List<List<Event>> eventBatches = splitIntoBatchesStream(eventList, batchSize);
        eventBatches.forEach(eventCleanerService::deleteSelectedListEventsAsync);
    }

    private List<List<Event>> splitIntoBatchesStream(List<Event> events, int batchSize) {
        return IntStream.range(0, (events.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> events.subList(i * batchSize, Math.min((i + 1) * batchSize, events.size())))
                .toList();
    }
}
