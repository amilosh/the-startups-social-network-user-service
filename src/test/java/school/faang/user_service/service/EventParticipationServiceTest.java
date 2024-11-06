package school.faang.user_service.service;

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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void registerParticipantTest() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(Collections.emptyList());
        eventParticipationService.registerParticipant(1L, 10L);
        Mockito.verify(eventParticipationRepository).register(1L, 10L);
    }

    @Test
    public void unregisterParticipantTest() {
        User user = User.builder().id(1L).username("name").build();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        eventParticipationService.unregisterParticipant(1L, 1L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 1L);
    }

    @Test
    public void registerParticipantThrowExceptionTest() {
        User user = User.builder().id(1L).username("name").build();
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.registerParticipant(1L, 1L));
    }

    @Test
    public void unregisterParticipantUserNotRegisteredThrowsUserNotRegisteredAtEventExceptionTest() {
        assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.unregisterParticipant(1L, 2L));
    }

    @Test
    public void getParticipantTest() {
        User user = User.builder().id(1L).username("name").build();
        UserDto userDto = UserDto.builder().id(1L).username("name").email("mail").build();
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Mockito.when(userMapper.toDto(user)).thenReturn(userDto);
        assertEquals(userDto, eventParticipationService.getListOfParticipant(1L).get(0));
    }

    @Test
    public void testUnregisterParticipation_UserNorRegistered() {
        long eventId = 1L;
        long userId = 2L;
        Exception exception = assertThrows(RuntimeException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId));
        assertEquals("Пользователь не зарегистрирован на событие", exception.getMessage());
    }


    @Test
    public void testRegisterParticipation_NullEventId() {
        long userId = 2L;
        Exception exception = assertThrows (IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(null, userId));
        assertEquals("There is not event with this ID!", exception.getMessage());
    }

    @Test
    public void testRegisterParticipation_NullUserId() {
        long eventId = 1L;
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(eventId, null));
        assertEquals("User ID cannot be null", exception.getMessage());
    }

}
