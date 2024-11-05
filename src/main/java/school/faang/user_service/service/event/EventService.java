package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataNotMatchException;
import school.faang.user_service.exception.EntityNotFoundExceptionWithID;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final SkillRepository skillRepository;
    @Setter
    private List<EventFilter> eventFilters;

    public ResponseEntity<EventDto> create(EventDto eventDto) throws DataNotMatchException {
        checkUserSkillsWithRelatedSkills(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        log.info("event success created {}", event);
        return ResponseEntity.status(201).body(eventMapper.toDto(eventRepository.save(event)));
    }

    public ResponseEntity<EventDto> getEvent(Long eventId) throws EntityNotFoundExceptionWithID {
        EventDto eventDto = eventMapper.toDto(getEventById(eventId));

        log.info("success get event dto: {}", eventDto);
        return ResponseEntity.status(200).body(eventDto);
    }

    public ResponseEntity<EventDto> deleteEvent(Long eventId) throws EntityNotFoundExceptionWithID {
        Event event = getEventById(eventId);

        log.info("success deleted event by id: {}", event);
        eventRepository.deleteById(eventId);
        return ResponseEntity.status(200).body(eventMapper.toDto(event));
    }

    public ResponseEntity<List<EventDto>> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> events = eventRepository.findAll().stream();
        log.info("get event filters {}", eventFilterDto.toString());

        List<EventDto> eventDtos = eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilterDto))
                .flatMap(filter -> filter.apply(events, eventFilterDto))
                .map(eventMapper::toDto)
                .toList();

        log.info("got events by filter: {}", eventDtos);
        return ResponseEntity.status(200).body(eventDtos);
    }

    public ResponseEntity<EventDto> updateEvent(EventDto eventDto) throws EntityNotFoundExceptionWithID {
        if (eventRepository.existsById(eventDto.getId())) {
            checkUserSkillsWithRelatedSkills(eventDto);

            Event event = eventMapper.toEntity(eventDto);
            log.info("event success updated {}", event);
            return ResponseEntity.status(200).body(eventMapper.toDto(eventRepository.save(event)));
        }

        String warnMessage = String.format("not such event by id: %d", eventDto.getId());
        log.warn(warnMessage);
        throw new EntityNotFoundExceptionWithID(warnMessage, eventDto.getId());
    }

    public ResponseEntity<List<EventDto>> getOwnedEvents(Long userId) {
        List<EventDto> events = eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
        log.info("got OWNED events: {}, by userId: {}", events, userId);
        return ResponseEntity.status(200).body(events);
    }

    public ResponseEntity<List<EventDto>> getParticipatedEvents(long userId) {
        List<EventDto> events = eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
        log.info("get PARTICIPATED events: {}, by userId: {}", events, userId);
        return ResponseEntity.status(200).body(events);
    }

    public void checkUserSkillsWithRelatedSkills(EventDto eventDto) throws DataNotMatchException {
        List<String> userSkillsTitles = skillRepository.
                findAllByUserId(eventDto.getOwnerId()).stream()
                .map(skill -> skill.getTitle().toLowerCase())
                .toList();

        eventDto.getRelatedSkills().stream()
                .filter(relatedSkill -> !userSkillsTitles.contains(relatedSkill.getTitle().toLowerCase()))
                .findAny()
                .ifPresent(skill -> {
                    log.warn("creating event wasn't successfully {}", eventDto);
                    throw new DataNotMatchException("you can't create such an event with such skills", skill);
                });
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    String warnMessage = String.format("not found such event by id:%d", eventId);
                    log.warn(warnMessage);
                    return new EntityNotFoundExceptionWithID(warnMessage, eventId);
                });
    }
}