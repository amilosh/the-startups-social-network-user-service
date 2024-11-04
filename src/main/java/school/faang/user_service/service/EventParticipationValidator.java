package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

@Component
public class EventParticipationValidator {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;

    public EventParticipationValidator(EventParticipationRepository eventParticipationRepository,
                                       EventRepository eventRepository) {
        this.eventParticipationRepository = eventParticipationRepository;
        this.eventRepository = eventRepository;
    }

    public void validateParticipation(Long eventId, Long userId) {
        checkNull(eventId, userId);
        checkEventExists(eventId);
    }

    public void validateRegisterParticipation(Long eventId, Long userId) {
        checkNull(eventId, userId);
        checkEventExists(eventId);

        if (eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalArgumentException("Пользователь уже зарегистрирован на это событие");
        }
    }

    public void validateEventId(Long eventId) {
        checkNull(eventId);
        checkEventExists(eventId);
    }

    public void validateUserRegistration(Long eventId, Long userId) {
        checkNull(eventId, userId);
        if (!eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalArgumentException("Пользователь не зарегистрирован на это событие.");
        }
    }

    private void checkNull(Long... ids) {
        for (Long id : ids) {
            if (id == null) {
                throw new IllegalArgumentException("Event ID и User ID не могут быть null");
            }
        }
    }

    private void checkEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Событие с id " + eventId + " не существует");
        }
    }
}
