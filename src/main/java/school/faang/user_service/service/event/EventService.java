package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventDto create(EventDto eventDto) {
        validateEventDates(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        log.info("Создаем событие: {}", event.getTitle());
        validateUserHasSkillsForEvent(event);
        Event savedEvent = eventRepository.save(event);
        log.info("Событие с ID: {}, успешно создано", savedEvent.getId());
        return eventMapper.toDto(savedEvent);
    }

    @Transactional(readOnly = true)
    public EventDto getEvent(Long eventId) {
        log.info("Ищем событие с ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с ID " + eventId + " не найдено"));
        return eventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> eventStream = eventRepository.findAll().stream();

        eventStream = eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filters))
                .reduce(eventStream,
                        (currentStream, eventFilter) -> eventFilter.apply(currentStream, filters),
                        (s1, s2) -> s2);

        List<Event> filteredEvents = eventStream.toList();

        log.info("По заданным фильтрам найдено: {} записей", filteredEvents.size());
        return eventMapper.toDtoList(filteredEvents);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getOwnedEvents(Long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        log.info("Для пользователя с ID: {}, найдено {} записей которые он создал", userId, events.size());
        return eventMapper.toDtoList(events);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getParticipatedEvents(Long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        log.info("Для пользователя с ID: {}, найдено {} записей в которых он принимает участие", userId, events.size());
        return eventMapper.toDtoList(events);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        log.info("Удаляем событие с ID: {}", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Событие с ID " + eventId + " не найдено");
        }
        eventRepository.deleteById(eventId);
        log.info("Удалили событие с ID: {}", eventId);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        Event existingEvent = getExistingEvent(eventDto.getId());
        log.info("Обновляем событие с ID: {}", existingEvent.getId());

        validateEventOwnership(existingEvent, eventDto.getOwnerId());
        validateEventDates(eventDto);
        validateUserHasSkillsForEvent(existingEvent);

        eventMapper.updateEntityFromDto(eventDto, existingEvent);
        Event updatedEvent = eventRepository.save(existingEvent);
        log.info("Событие с ID: {} успешно обновлено", updatedEvent.getId());
        return eventMapper.toDto(updatedEvent);
    }

    private Event getExistingEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с ID " + eventId + " не найдено"));
    }

    private void validateEventOwnership(Event existingEvent, Long ownerId) {
        if (!existingEvent.getOwner().getId().equals(ownerId)) {
            throw new DataValidationException("Пользователь с ID " + ownerId + " не является владельцем события");
        }
    }

    private void validateUserHasSkillsForEvent(Event event) {
        log.info("Проверяем навыки пользователя с ID: {}", event.getOwner().getId());
        Long userId = event.getOwner().getId();
        User user = userRepository.findByIdWithSkills(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID: " + userId + " не найден"));
        Set<String> userSkills = user.getSkills().stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet());
        boolean hasAllSkills = event.getRelatedSkills().stream()
                .map(Skill::getTitle)
                .allMatch(userSkills::contains);
        if (!hasAllSkills) {
            throw new DataValidationException("Пользователь с ID: " + userId + " не имеет необходимых навыков");
        }
    }

    private void validateEventDates(EventDto eventDto) {
        if (eventDto.getEndDate().isBefore(eventDto.getStartDate())) {
            throw new DataValidationException("Дата окончания события не может быть раньше даты начала.");
        }
    }
}
