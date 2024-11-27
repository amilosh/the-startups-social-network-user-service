package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventValidator eventValidator;

    @Mock
    public UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    void testRegisterParticipant() {
        long eventId = 2L;
        long userId = 1L;

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventValidator).validateEventExists(eventId);
        verify(eventValidator).validateUserNotRegistered(eventId, userId);
        verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    void testUnregisterParticipant() {
        long eventId = 2L;
        long userId = 1L;

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventValidator).validateEventExists(eventId);
        verify(eventValidator).validateUserIsRegistered(eventId, userId);
        verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    void testGetParticipants() {
        long eventId = 2L;

        List<User> users = Arrays.asList(new User(), new User());
        List<UserDto> userDtos = Arrays.asList(new UserDto(), new UserDto());

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(users);
        when(userMapper.toListDto(users)).thenReturn(userDtos);

        List<UserDto> participants = eventParticipationService.getParticipants(eventId);

        assertEquals(2, participants.size());
        verify(eventValidator).validateEventExists(eventId);
        verify(eventParticipationRepository).findAllParticipantsByEventId(eventId);
        verify(userMapper).toListDto(users);
    }

    @Test
    void testGetParticipantsCount() {
        long eventId = 2L;
        int count = 5;

        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(count);

        int participantCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(5, participantCount);
        verify(eventValidator).validateEventExists(eventId);
        verify(eventParticipationRepository).countParticipants(eventId);
    }
}