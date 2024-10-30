package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipation(Long eventId, Long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        for (User user : participants) {
            if (user.getId().equals(userId)) {
                throw new IllegalArgumentException("Пользователь уже зарегистрирован на это событие.");
            }
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipation(Long eventId, Long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        // Проверяем, зарегистрирован ли пользователь на событие
        boolean userExists = participants.stream().anyMatch(user -> user.getId().equals(userId));
        if (!userExists) {
            throw new IllegalArgumentException("Пользователь не зарегистрирован на это событие.");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    // Возвращаем список участников события
    public List<User> getListOfParticipants(Long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    // Возвращаем количество зарегистрированных участников
    public int getCountOfParticipants(Long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

}
