package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public void registerParticipant(UserDto userDto, EventDto eventDto) {
        if (isParticipantRegistered(userDto, eventDto)) {
            throw new DataValidationException("Пользователь уже зарегистрирован на событие");
        }
        eventParticipationRepository.register(userDto.id(), eventDto.id());
    }

    public void unregisterParticipant(UserDto userDto, EventDto eventDto) {
        if (!isParticipantRegistered(userDto, eventDto)) {
            throw new DataValidationException("Пользователь не был зарегистрирован на событие");
        }
        eventParticipationRepository.unregister(userDto.id(), eventDto.id());
    }

    public List<UserDto> getParticipants(EventDto eventDto) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventDto.id());

        return participants.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public int getParticipantsCount(EventDto eventDto) {
        return eventParticipationRepository.countParticipants(eventDto.id());
    }

    private boolean isParticipantRegistered(UserDto userDto, EventDto eventDto) {
        User user = userService.getUserById(userDto.id());
        List<Long> participantsId = eventParticipationRepository.findAllParticipantsByEventId(eventDto.id()).stream()
                .map(User::getId)
                .toList();
        return participantsId.contains(user.getId());
    }
}
