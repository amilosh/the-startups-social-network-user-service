package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(UserDto userDto, EventDto eventDto) throws Exception {
        participantProcess(userDto, eventDto, true);
    }

    public void unregisterParticipant(UserDto userDto, EventDto eventDto) throws Exception {
        participantProcess(userDto, eventDto, false);
    }

    public List<UserDto> getParticipant(EventDto eventDto) {
        Event event = EventMapper.toEntity(eventDto);
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(event.getId());

        return participants.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public long getParticipationCount(EventDto eventDto) {
        Event event = EventMapper.toEntity(eventDto);
        return eventParticipationRepository.countParticipants(event.getId());
    }

    private void participantProcess(UserDto userDto, EventDto eventDto, boolean shouldBeRegistered) {
        User user = UserMapper.toEntity(userDto);
        Event event = EventMapper.toEntity(eventDto);
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventDto.id());

        boolean isRegistered = participants.contains(user);

        if (shouldBeRegistered && isRegistered) {
            throw new IllegalStateException("Пользователь уже зарегистрирован на событие");
        }

        if (!shouldBeRegistered && !isRegistered) {
            throw new NoSuchElementException("Пользователь не был зарегистрирован на событие");
        }

        if (shouldBeRegistered) {
            eventParticipationRepository.register(user.getId(), event.getId());
        } else {
            eventParticipationRepository.unregister(user.getId(), event.getId());
        }
    }
}
