package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.dto.UserDto;
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
import static org.mockito.Mockito.when;

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
    void registerParticipantWithoutExceptionTest() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(eventParticipationRepository.checkUserRegistrationForEvent(1L, 2L)).thenReturn(false);
        assertDoesNotThrow(() -> eventParticipationService.registerParticipant(1L,2L));
    }

    @Test
    void registerParticipantWithIllegalArgumentExceptionForEventTest() {
        when(eventRepository.existsById(100L)).thenReturn(false);
        when(userRepository.existsById(1L)).thenReturn(true);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(100L,1L));
        String expectedMessage = "Event with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void registerParticipantWithIllegalArgumentExceptionForUserTest() {
        when(userRepository.existsById(100L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(3L,100L));
        String expectedMessage = "User with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void registerParticipantWithIllegalArgumentExceptionForUserAlreadyRegisteredTest() {
        when(eventRepository.existsById(3L)).thenReturn(true);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(eventParticipationRepository.checkUserRegistrationForEvent(3L, 1L)).thenReturn(true);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(3L,1L));
        String expectedMessage = "User with id: 1 already registered for the event: 3";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void unregisterParticipantWithoutExceptionTest() {
        when(eventRepository.existsById(5L)).thenReturn(true);
        when(userRepository.existsById(7L)).thenReturn(true);
        when(eventParticipationRepository.checkUserRegistrationForEvent(5L, 7L)).thenReturn(true);
        assertDoesNotThrow(() -> eventParticipationService.unregisterParticipant(5L,7L));
    }

    @Test
    void unregisterParticipantWithIllegalArgumentExceptionForEventTest() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.existsById(100L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(100L,1L));
        String expectedMessage = "Event with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void unregisterParticipantWithIllegalArgumentExceptionForUserTest() {
        when(userRepository.existsById(100L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(3L,100L));
        String expectedMessage = "User with id: 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void unregisterParticipantWithIllegalArgumentExceptionForUserIsNotRegisteredTest() {
        when(eventRepository.existsById(3L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(eventParticipationRepository.checkUserRegistrationForEvent(3L, 2L)).thenReturn(false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(3L,2L));
        String expectedMessage = "User with id: 2 is not registered for the event: 3";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getParticipantWithOneElementTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("JohnDoe");
        user.setEmail("johndoe@example.com");
        UserDto userDto = new UserDto(1L, "JohnDoe", "johndoe@example.com");
        when(eventParticipationRepository.findAllParticipantsByEventId(3)).thenReturn(List.of(user));
        when(userMapper.userListToUserDtoList(List.of(user))).thenReturn(List.of(userDto));
        assertDoesNotThrow(() -> eventParticipationService.getParticipant(3L));
        List<UserDto> participant = eventParticipationService.getParticipant(3L);
        assertEquals(1, participant.size());
        assertEquals(1, participant.get(0).id());
        assertEquals("johndoe@example.com", participant.get(0).email());
        assertEquals("JohnDoe", participant.get(0).username());
    }

    @Test
    void getParticipantWithEmptyListTest() {
        when(eventParticipationRepository.findAllParticipantsByEventId(4)).thenReturn(Collections.emptyList());
        when(userMapper.userListToUserDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> eventParticipationService.getParticipant(4L));
        List<UserDto> participant = eventParticipationService.getParticipant(4L);
        assertEquals(0, participant.size());
        Assertions.assertTrue(participant.isEmpty());
    }

    @Test
    void getParticipantsCountOnEmptyEventTest() {
        when(eventParticipationRepository.countParticipants(1L)).thenReturn(0);
        assertDoesNotThrow(() -> eventParticipationService.getParticipantsCount(1L));
        assertEquals(0, eventParticipationService.getParticipantsCount(1L));
    }

    @Test
    void getParticipantsCountOnEventWithThreeParticipantTest() {
        when(eventParticipationRepository.countParticipants(2L)).thenReturn(3);
        assertDoesNotThrow(() -> eventParticipationService.getParticipantsCount(2L));
        assertEquals(3, eventParticipationService.getParticipantsCount(2L));
    }
}
