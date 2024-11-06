package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;
@ControllerAdvice
@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(Long eventId, Long userId) {
        validateEventId(eventId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        boolean userAlreadyRegistered = users.stream().anyMatch(user -> user.getId().equals(userId));
        if (userAlreadyRegistered) {
            throw new IllegalArgumentException("Пользователь уже зарегистрирован.");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        if (checkThereIsUserInEvent(eventId, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new IllegalArgumentException("Пользователь не зарегистрирован на событие");
        }
    }

    public boolean checkThereIsUserInEvent(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public List<UserDto> getListOfParticipant(Long eventId) {
        validateEventId(eventId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        List<UserDto> userDto = new ArrayList<>();
        for (User user : users) {
            userDto.add(userMapper.toDto(user));
        }
        return userDto;
    }

    public int getCountRegisteredParticipant(Long eventId) {
        validateEventId(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }

    private void validateEventId(Long eventId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new IllegalArgumentException("There is not event with this ID!");
        }
    }
}