package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final SkillMapper skillMapper;
    private final List<EventFilter> eventFilters;


    public EventDto create(EventDto eventDto) {
        log.info("Processing creation of event with title: {}", eventDto.title());
        User userOwner = userService.getUserById(eventDto.ownerId());

        log.debug("Validating user skills for owner in createEvent: {}", userOwner);
        validateUserSkillsForEvent(userOwner, eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userOwner);

        if (eventExists(event)) {
            throw new DataValidationException("You do not create event, because this event already create");
        }

        Event savedEvent = eventRepository.save(event);
        log.info("Event saved with ID: {}", savedEvent.getId());

        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataValidationException("Event do not found"));
        log.info("Event found by {}", eventId);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        log.info("Fetching events with filters: {}", filters);

        Stream<Event> allEvents = eventRepository.findAll().stream();

        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(allEvents, filters));

        log.info("Events are filtered");

        return eventMapper.toDtoList(allEvents.toList());
    }

    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event not found");
        }
        eventRepository.deleteById(eventId);
        log.info("Event with ID: {} deleted", eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        User userOwner = userService.getUserById(eventDto.ownerId());

        log.debug("Validating user skills for owner in updateEvent: {}", userOwner);
        validateUserSkillsForEvent(userOwner, eventDto);

        Event event = eventMapper.toEntity(eventDto);
        Event updatedSavedEvent = eventRepository.save(event);
        log.info("Event are updated");

        return eventMapper.toDto(updatedSavedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> ownedEvents = userService.getUserById(userId).getOwnedEvents();
        log.info("Owned Events found by {}", userId);
        return eventMapper.toDtoList(ownedEvents);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEvents = userService.getUserById(userId).getParticipatedEvents();
        log.info("Participated Events found by {}", userId);
        return eventMapper.toDtoList(participatedEvents);
    }


    private void validateUserSkillsForEvent(User userOwner, EventDto eventDto) {
        List<SkillDto> relatedSkills = eventDto.relatedSkills();
        List<SkillDto> userOwnerSkills = skillMapper.toDtoList(userOwner.getSkills());

        log.debug("Checking if user has required skills: {}", relatedSkills);

        if (!userHasSkills(userOwnerSkills, relatedSkills)) {
            log.error("User {} doesn't have required skills to create event", userOwner.getId());
            throw new DataValidationException("User don't have required skills to create event");
        }

        log.info("User {} has all required skills to create event", userOwner.getId());
    }

    private boolean userHasSkills(List<SkillDto> userOwnerSkills, List<SkillDto> relatedSkills) {
        return relatedSkills.stream()
                .allMatch(relatedSkill -> userOwnerSkills.stream()
                        .anyMatch(userSkill -> userSkill.equals(relatedSkill))
                );
    }

    private boolean eventExists(Event event) {
        return eventRepository.existsByTitleAndStartDateAndEndDateAndOwnerAndLocationAndMaxAttendees(
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                event.getOwner(),
                event.getLocation(),
                event.getMaxAttendees()
        );
    }
}