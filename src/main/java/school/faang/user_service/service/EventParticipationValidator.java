package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

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
        if (eventId == null || userId == null) {
            throw new IllegalArgumentException("Event ID и User ID не могут быть null");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Событие с id " + eventId + " не существует");
        }
    }

    public void validateRegisterParticipation(Long eventId, Long userId) {
        boolean isAlreadyRegistered = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId().equals(userId));
        if (isAlreadyRegistered) {
            throw new IllegalArgumentException("Пользователь уже зарегистрирован на это событие");
        }
    }

    public void validateEventId(Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID не может быть null");
        }
    }
        public void validateUserRegistration (Long eventId, Long userId){
            List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
            boolean userExists = participants.stream().anyMatch(user -> user.getId().equals(userId));
            if (!userExists) {
                throw new IllegalArgumentException("Пользователь не зарегистрирован на это событие.");
            }
        }
    }
