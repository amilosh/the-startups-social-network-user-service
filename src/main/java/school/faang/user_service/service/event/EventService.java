package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final SkillRepository skillRepository;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        checkSkillsWithRelatedSkills(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        log.info("event success created {}", event);
        return eventMapper.toDto(eventRepository.save(event));
    }

    public EventDto getEvent(Long eventId) {
        Event event = getEventById(eventId);

        log.info("success get event: {}", event);
        return eventMapper.toDto(event);
    }

    public EventDto deleteEvent(Long eventId) {
        Event event = getEventById(eventId);

        log.info("success deleted event by id: {}", eventId);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        List<Event> events = eventRepository.findAll();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilterDto))
                .forEach(filter -> filter.apply(events, eventFilterDto));

        log.info("got events by filter: {}", events);
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public EventDto updateEvent(EventDto eventDto) {
        checkSkillsWithRelatedSkills(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        log.info("event success updated {}", event);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    private void checkSkillsWithRelatedSkills(EventDto eventDto) {
        skillRepository.findAllByUserId(eventDto.getOwnerId()).stream()
                .filter(skill -> {
                    return eventDto.getRelatedSkills().stream()
                            .allMatch(skillDto -> skillDto.getTitle().equals(skill.getTitle()));
                })
                .findAny()
                .ifPresent(skill -> {
                    log.warn("creating event wasn't successfully {}", eventDto);
                    throw new IllegalArgumentException("you can't create such an event with such skills");
                });
    }

    public Event getEventById(Long ownerId) {
        return eventRepository.findById(ownerId)
                .orElseThrow(() -> {
                    String warnMessage = String.format("not found such event by id:%d", ownerId);
                    log.warn(warnMessage);
                    return new IllegalArgumentException(warnMessage);
                });
    }
}