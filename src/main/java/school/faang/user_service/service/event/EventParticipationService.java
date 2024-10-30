package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;

    @Transactional
    public void registerParticipant(Long eventId, Long userId) {
        validateUserAndEvent(eventId, userId);
        if (checkUserRegistrationForEvent(eventId, userId)) {
            log.error("User with id: {} already registered for the event: {}", userId, eventId);
            throw new IllegalArgumentException(String.format("User with id: %s already registered for the event: %s", userId, eventId));
        }
        eventParticipationRepository.register(eventId,userId);
    }

    @Transactional
    public void unregisterParticipant(Long eventId, Long userId) {
        validateUserAndEvent(eventId, userId);
        if (!checkUserRegistrationForEvent(eventId, userId)) {
            log.error("User with id: {} is not registered for the event: {}", userId, eventId);
            throw new IllegalArgumentException(String.format("User with id: %s is not registered for the event: %s", userId, eventId));
        }
        eventParticipationRepository.unregister(eventId,userId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getParticipant(Long eventId) {
        return userMapper.userListToUserDtoList(eventParticipationRepository.findAllParticipantsByEventId(eventId));
    }

    @Transactional(readOnly = true)
    public long getParticipantsCount(Long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private void validateUserAndEvent(Long eventId, Long userId) {
        validateUser(userId);
        validateEvent(eventId);
    }

    private boolean checkUserRegistrationForEvent(Long eventId, Long userId) {
        return eventParticipationRepository.checkUserRegistrationForEvent(eventId, userId);
    }

    private void validateEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.error("Event with id: {} does not exist", eventId);
            throw new IllegalArgumentException(String.format("Event with id: %s does not exist", eventId));
        }
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User with id: {} does not exist", userId);
            throw new IllegalArgumentException(String.format("User with id: %s does not exist", userId));
        }
    }
}
