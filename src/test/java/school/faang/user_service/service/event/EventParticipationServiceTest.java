package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.exception.DataValidationException;

import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.EventParticipationService;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void testRegisterParticipant() {
        when(eventParticipationRepository.existsByEventIdAndUserId(10L, 1L)).thenReturn(false);
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(Collections.emptyList());
        eventParticipationService.registerParticipant(1L, 10L);
    }

    @Test
    void testUserAlreadyRegistered() {
        Long eventId = 1L;
        Long userId = 1L;
        when(eventParticipationRepository.existsByEventIdAndUserId(userId, eventId)).thenReturn(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
        assertNotNull(exception);
    }

    @Test
    public void testRegisterParticipantThrowException() {
        User user = User.builder().id(1L).username("name").build();
        when(eventParticipationRepository.existsByEventIdAndUserId(1L, 1L)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(1L, 1L));
    }


    @Test
    public void testUnregisterParticipant() {
        User user = User.builder().id(1L).username("name").build();
        when(eventParticipationRepository.existsByEventIdAndUserId(1L, 1L)).thenReturn(true);
        eventParticipationService.unregisterParticipant(1L, 1L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 1L);
    }

    @Test
    public void testUnregisterParticipantThrowsException() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.unregisterParticipant(1L, 2L));
    }

    @Test
    public void testUnregisterParticipation_UserNorRegistered() {
        long eventId = 1L;
        long userId = 2L;
        Exception exception = assertThrows(DataValidationException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId));
    }

    @Test
    public void testGetParticipant() {
        User user = User.builder().id(1L).username("name").build();
        UserDto userDto = UserDto.builder().id(1L).username("name").email("mail").build();
        when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        assertEquals(userDto, eventParticipationService.getListOfParticipant(1L).get(0));
    }


    @Test
    public void testRegisterParticipation_NullEventId() {
        long userId = 2L;
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(null, userId));
    }

    @Test
    public void testRegisterParticipation_NullUserId() {
        long eventId = 1L;
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(eventId, null));
    }

}
