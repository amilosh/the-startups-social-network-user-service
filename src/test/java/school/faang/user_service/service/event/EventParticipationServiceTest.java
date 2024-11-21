package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    void registerParticipantSuccessTest() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(eventParticipationRepository.checkUserRegistrationForEvent(1L, 2L)).thenReturn(false);
        assertDoesNotThrow(() -> eventParticipationService.registerParticipant(1L,2L));
        verify(eventRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).existsById(2L);
        verify(eventParticipationRepository, times(1)).checkUserRegistrationForEvent(1L, 2L);
        verify(eventParticipationRepository, times(1)).register(1L, 2L);
    }

    @Test
    void registerParticipantForNonExistentEventFailTest() {
        when(eventRepository.existsById(100L)).thenReturn(false);
        when(userRepository.existsById(1L)).thenReturn(true);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(100L,1L));
        String expectedMessage = "Event with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(eventRepository, times(1)).existsById(100L);
        verify(userRepository, times(1)).existsById(1L);
        verify(eventParticipationRepository, times(0)).checkUserRegistrationForEvent(100L, 1L);
        verify(eventParticipationRepository, times(0)).register(100L, 1L);
    }

    @Test
    void registerParticipantForNonExistentUserFailTest() {
        when(userRepository.existsById(100L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(3L,100L));
        String expectedMessage = "User with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(eventRepository, times(0)).existsById(3L);
        verify(userRepository, times(1)).existsById(100L);
        verify(eventParticipationRepository, times(0)).checkUserRegistrationForEvent(3L, 100L);
        verify(eventParticipationRepository, times(0)).register(3L, 100L);
    }

    @Test
    void registerParticipantForAlreadyRegisteredUserFailTest() {
        when(eventRepository.existsById(3L)).thenReturn(true);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(eventParticipationRepository.checkUserRegistrationForEvent(3L, 1L)).thenReturn(true);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(3L,1L));
        String expectedMessage = "User with id: 1 already registered for the event: 3";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(eventRepository, times(1)).existsById(3L);
        verify(userRepository, times(1)).existsById(1L);
        verify(eventParticipationRepository, times(0)).checkUserRegistrationForEvent(100L, 1L);
        verify(eventParticipationRepository, times(0)).register(100L, 1L);
    }

    @Test
    void unregisterParticipantSuccessTest() {
        when(eventRepository.existsById(5L)).thenReturn(true);
        when(userRepository.existsById(7L)).thenReturn(true);
        when(eventParticipationRepository.checkUserRegistrationForEvent(5L, 7L)).thenReturn(true);
        assertDoesNotThrow(() -> eventParticipationService.unregisterParticipant(5L,7L));
        verify(eventRepository, times(1)).existsById(5L);
        verify(userRepository, times(1)).existsById(7L);
        verify(eventParticipationRepository, times(1)).checkUserRegistrationForEvent(5L, 7L);
        verify(eventParticipationRepository, times(1)).unregister(5L, 7L);
    }

    @Test
    void unregisterParticipantForNonExistentEventFailTest() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.existsById(100L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(100L,1L));
        String expectedMessage = "Event with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(eventRepository, times(1)).existsById(100L);
        verify(userRepository, times(1)).existsById(1L);
        verify(eventParticipationRepository, times(0)).checkUserRegistrationForEvent(100L, 1L);
        verify(eventParticipationRepository, times(0)).register(100L, 1L);
    }

    @Test
    void unregisterParticipantForNonExistentUserFailTest() {
        when(userRepository.existsById(100L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(3L,100L));
        String expectedMessage = "User with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(eventRepository, times(0)).existsById(1L);
        verify(userRepository, times(1)).existsById(100L);
        verify(eventParticipationRepository, times(0)).checkUserRegistrationForEvent(1L, 100L);
        verify(eventParticipationRepository, times(0)).register(1L, 100L);
    }

    @Test
    void unregisterParticipantForNonRegisteredUserFailTest() {
        when(eventRepository.existsById(3L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(eventParticipationRepository.checkUserRegistrationForEvent(3L, 2L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(3L,2L));
        String expectedMessage = "User with id: 2 is not registered for the event: 3";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(eventRepository, times(1)).existsById(3L);
        verify(userRepository, times(1)).existsById(2L);
        verify(eventParticipationRepository, times(1)).checkUserRegistrationForEvent(3L, 2L);
        verify(eventParticipationRepository, times(0)).register(3L, 2L);
    }

    @Test
    void getParticipantSuccessTest() {
        User user = User.builder()
                .id(1L)
                .username("JohnDoe")
                .email("johndoe@example.com")
                .build();
        UserDto userDto = new UserDto(1L, "JohnDoe", "johndoe@example.com");
        when(eventParticipationRepository.findAllParticipantsByEventId(3)).thenReturn(List.of(user));
        when(userMapper.userListToUserDtoList(List.of(user))).thenReturn(List.of(userDto));
        assertDoesNotThrow(() -> {
            List<UserDto> participant = eventParticipationService.getParticipant(3L);
            assertEquals(1, participant.size());
            assertEquals(1, participant.get(0).id());
            assertEquals("johndoe@example.com", participant.get(0).email());
            assertEquals("JohnDoe", participant.get(0).username());
        });
        verify(userMapper, times(1)).userListToUserDtoList(List.of(user));
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(3L);
    }

    @Test
    void getParticipantForEmptyEventSuccessTest() {
        when(eventParticipationRepository.findAllParticipantsByEventId(4)).thenReturn(Collections.emptyList());
        when(userMapper.userListToUserDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> {
            List<UserDto> participant = eventParticipationService.getParticipant(4L);
            assertEquals(0, participant.size());
            assertTrue(participant.isEmpty());
        });
        verify(userMapper, times(1)).userListToUserDtoList(Collections.emptyList());
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(4L);
    }

    @Test
    void getParticipantsCountForEmptyEventSuccessTest() {
        when(eventParticipationRepository.countParticipants(1L)).thenReturn(0);
        assertDoesNotThrow(() -> assertEquals(0, eventParticipationService.getParticipantsCount(1L)));
        verify(eventParticipationRepository, times(1)).countParticipants(1L);
    }

    @Test
    void getParticipantsCountForEventWithThreeParticipantsSuccessTest() {
        when(eventParticipationRepository.countParticipants(2L)).thenReturn(3);
        assertDoesNotThrow(() -> assertEquals(3, eventParticipationService.getParticipantsCount(2L)));
        verify(eventParticipationRepository, times(1)).countParticipants(2L);
    }
}
