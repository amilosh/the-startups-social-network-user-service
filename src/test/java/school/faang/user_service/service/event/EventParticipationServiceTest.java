package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    private final long EVENT_ID = 1L;
    private final long USER_ID = 1L;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    void testRegisterRegisteredParticipant() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(USER_ID);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(List.of(user));
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(EVENT_ID, USER_ID));
    }

    @Test
    void testRegisterNotRegisteredParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        eventParticipationService.registerParticipant(EVENT_ID, USER_ID);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).register(EVENT_ID, USER_ID);
    }

    @Test
    void testUnregisterRegisteredParticipant() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(USER_ID);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(Mockito.anyLong())).thenReturn(List.of(user));
        eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).unregister(EVENT_ID, USER_ID);
    }

    @Test
    void testUnregisterNotRegisteredParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID));
    }

    @Test
    void testGetParticipants(){
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        List<User> users = new ArrayList<>(List.of(user1, user2));
        UserDto userDto1 = new UserDto();
        UserDto userDto2 = new UserDto();
        userDto1.setId(1L);
        userDto2.setId(2L);
        List<UserDto> expected = new ArrayList<>();
        expected.add(userDto1);
        expected.add(userDto2);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(users);
        Mockito.when(userMapper.toDto(user1)).thenReturn(userDto1);
        Mockito.when(userMapper.toDto(user2)).thenReturn(userDto2);
        List<UserDto> result = eventParticipationService.getParticipant(EVENT_ID);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetParticipantsCount(){
        int count = 3;
        Mockito.when(eventParticipationRepository.countParticipants(EVENT_ID)).thenReturn(count);
        int result = eventParticipationService.getParticipantsCount(EVENT_ID);
        Assertions.assertEquals(count, result);
    }

    @Test
    void testIsNotRegisteredParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        Assertions.assertFalse(eventParticipationService.isRegisteredParticipant(EVENT_ID, USER_ID));
    }

    @Test
    void testIsRegisteredParticipant() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(USER_ID);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(new ArrayList<>(List.of(user)));
        Assertions.assertTrue(eventParticipationService.isRegisteredParticipant(EVENT_ID, USER_ID));
    }

}