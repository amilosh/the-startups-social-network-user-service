package school.faang.user_service.service.eventService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.EventParticipationValidator;
import school.faang.user_service.service.userService.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final EventParticipationValidator validator;
    private final UserService userService;

    public void registerParticipation(Long eventId, Long userId) {
        validator.validateParticipation(eventId, userId); // Проверяем, существует ли событие и пользователь
        validator.validateRegisterParticipation(eventId, userId); // Проверяем, не зарегистрирован ли пользователь
        eventParticipationRepository.register(eventId, userId); // Регистрируем участие
    }

    public void unregisterParticipation(Long eventId, Long userId) {
        validator.validateParticipation(eventId, userId); // Проверяем, существует ли событие и пользователь
        validator.validateUserRegistration(eventId, userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    // Возвращаем список участников события
    public List<UserDto> getListOfParticipants(Long eventId) {
        validator.validateEventId(eventId);
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return participants.stream()
                .map(userService::convertToDto) // Используем метод из userService
                .collect(Collectors.toList());
    }

    // Возвращаем количество зарегистрированных участников
    public int getParticipantsCount(long eventId) {
        validator.validateEventId(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }
}