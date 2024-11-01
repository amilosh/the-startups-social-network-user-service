package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final EventValidator eventValidator;

    public void registerParticipant(UserDto userDto, EventDto eventDto) {
        participantProcess(userDto, eventDto, true);
    }

    public void unregisterParticipant(UserDto userDto, EventDto eventDto) {
        participantProcess(userDto, eventDto, false);
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

    private void participantProcess(UserDto userDto, EventDto eventDto, boolean shouldBeRegistered) {
        User user = userService.getUserById(userDto.id());
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventDto.id());
        boolean isRegistered = participants.contains(user);

        eventValidator.validateRegistration(isRegistered, shouldBeRegistered);

        if (shouldBeRegistered) {
            eventParticipationRepository.register(user.getId(), eventDto.id());
        } else {
            eventParticipationRepository.unregister(user.getId(), eventDto.id());
        }
    }
}
