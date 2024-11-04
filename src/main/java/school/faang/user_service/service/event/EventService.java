package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
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

    public EventDto create(EventDto eventDto) {
        checkUserSkillsWithRelatedSkills(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        log.info("event success created {}", event);
        return eventMapper.toDto(eventRepository.save(event));
    }

    public EventDto getEvent(Long eventId) {
        EventDto eventDto = eventMapper.toDto(getEventById(eventId));

        log.info("success get event dto: {}", eventDto);
        return eventDto;
    }

    public EventDto deleteEvent(Long eventId) {
        Event event = getEventById(eventId);

        log.info("success deleted event by id: {}", event);
        eventRepository.deleteById(eventId);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> events = eventRepository.findAll().stream();
        log.info("get event filters {}", eventFilterDto.toString());

        List<EventDto> eventDtos = eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilterDto))
                .flatMap(filter -> filter.apply(events, eventFilterDto))
                .map(eventMapper::toDto)
                .toList();

        log.info("got events by filter: {}", eventDtos);
        return eventDtos;
    }

    public EventDto updateEvent(EventDto eventDto) {
        if (eventRepository.existsById(eventDto.getId())) {
            checkUserSkillsWithRelatedSkills(eventDto);

            Event event = eventMapper.toEntity(eventDto);
            log.info("event success updated {}", event);
            return eventMapper.toDto(eventRepository.save(event));
        }
        String warnMessage = String.format("not such event by id: %d", eventDto.getId());
        log.warn(warnMessage);
        throw new IllegalArgumentException(warnMessage);
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        List<EventDto> events = eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
        log.info("got OWNED events: {}, by userId: {}", events, userId);
        return events;
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<EventDto> events = eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
        log.info("get PARTICIPATED events: {}, by userId: {}", events, userId);
        return events;
    }

    public void checkUserSkillsWithRelatedSkills(EventDto eventDto) {
        List<String> userSkillsTitles = skillRepository.
                findAllByUserId(eventDto.getOwnerId()).stream()
                .map(skill -> skill.getTitle().toLowerCase())
                .toList();

        eventDto.getRelatedSkills().stream()
                .filter(relatedSkill -> !userSkillsTitles.contains(relatedSkill.getTitle().toLowerCase()))
                .findAny()
                .ifPresent(skill -> {
                    log.warn("creating event wasn't successfully {}", eventDto);
                    throw new IllegalArgumentException("you can't create such an event with such skills");
                });
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    String warnMessage = String.format("not found such event by id:%d", eventId);
                    log.warn(warnMessage);
                    return new IllegalArgumentException(warnMessage);
                });
    }
}