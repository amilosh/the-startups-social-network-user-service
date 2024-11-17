package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;


    public EventDto create(EventDto eventDto) {
        if (!hasRequiredSkills(eventDto)) {
            throw new IllegalArgumentException("User does not have the required skills to conduct this event");
        }
        Event event = eventMapper.toEntity(eventDto);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    private boolean hasRequiredSkills(EventDto event) {
        List<Long> requiredSkillIds = event.getRelatedSkills().stream().map(SkillDto::getId).collect(Collectors.toList());
        List<Long> ownerSkillIds = skillRepository.findAllByUserId(event.getOwnerId()).stream().map(Skill::getId).collect(Collectors.toList());
        return ownerSkillIds.containsAll(requiredSkillIds);
    }

    public EventDto getEvent(long eventId) {
        Optional<Event> foundEvent = eventRepository.findById(eventId);
        if (foundEvent.isPresent()) {
            Event gotEvent = foundEvent.get();
            return eventMapper.toDto(gotEvent);
        } else throw new IllegalArgumentException("This event is not exist");
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();
        return filterEvents(events, filter);
    }

    public long deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
        return eventId;
    }

    public EventDto updateEvent(EventDto event) {
        if (!hasRequiredSkills(event)) {
            throw new IllegalArgumentException("The user cannot update the event, since doesn't have the required skills");
        }
        Event eventEntity = eventMapper.toEntity(event);
        Event savedEvent = eventRepository.save(eventEntity);
        return eventMapper.toDto(savedEvent);

    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return eventMapper.toDtoList(events);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toDtoList(events);
    }

    public List<EventDto> filterEvents(List<Event> events, EventFilterDto filter) {
        return events.stream()
                .filter(event -> filter.getTitle() == null || event.getTitle().contains(filter.getTitle()))
                .filter(event -> filter.getStartDateFrom() == null || !event.getStartDate().isBefore(filter.getStartDateFrom().atStartOfDay()))
                .filter(event -> filter.getStartDateTo() == null || !event.getStartDate().isAfter(filter.getStartDateTo().atStartOfDay()))
                .filter(event -> filter.getOwnerId() == null || event.getOwner().getId() == (filter.getOwnerId()))
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }
}


