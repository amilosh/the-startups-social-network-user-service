package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.ParticipationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.userService.UserService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Component
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        if (eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new ParticipationException("Пользователь уже зарегистрирован на это событие.");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (!eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new ParticipationException("Пользователь не зарегистрирован на это событие.");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<User> getParticipant(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event with ID " + eventId + " does not exist");
        }
        return eventParticipationRepository.findUsersByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event with ID " + eventId + " does not exist");
        }
        int participantsCount = eventParticipationRepository.countParticipants(eventId);
        if (participantsCount == 0) {
            throw new EventNotFoundException("No participants found for the event with ID " + eventId);
        }
        return participantsCount;
    }
}
