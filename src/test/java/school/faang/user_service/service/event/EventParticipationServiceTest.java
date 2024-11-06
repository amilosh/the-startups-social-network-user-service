package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.EventParticipationRepository;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Component
@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void registerParticipantTest() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(Collections.emptyList());
        eventParticipationService.registerParticipant(1L, 10L);
        verify(eventParticipationRepository).register(1L, 10L);
        verify(eventParticipationRepository, times(1)).countParticipants(1L);
    }

    @Test
    public void testRegisterParticipation_UserAlreadyRegistered() {
        long eventId = 1L;
        long userId = 1L;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(1);
        Exception exception = assertThrows(RuntimeException.class, () -> eventParticipationService.
                registerParticipant(eventId, userId));
        assertEquals("Пользователь уже зарегистрирован на событие", exception.getMessage());
    }

    @Test
    public void testUnregisterParticipation_Success() {
        long eventId = 1L;
        long userId = 2L;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(1);
        eventParticipationService.unregisterParticipant(eventId, userId);
        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    public void testUnregisterParticipation_UserNorRegistered() {
        long eventId = 1L;
        long userId = 2L;
        Exception exception = assertThrows(RuntimeException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId));
        assertEquals("Пользователь не зарегисрирован на событие", exception.getMessage());
    }

    @Test
    public void testGetParticipantCount() {
        long eventId = 1L;
        int expectedCount = 5;

        Mockito.when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getCountRegisteredParticipant(eventId);

        assertEquals(expectedCount, actualCount);

        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
    }

    @Test
    public void testRegisterParticipation_NullEventId() {
        long userId = 2L;
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(null, userId));
        assertEquals("Event ID cannot be null", exception.getMessage());
    }

    @Test
    public void testRegisterParticipation_NullUserId() {
        long eventId = 1L;
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(eventId, null));
        assertEquals("User ID cannot be null", exception.getMessage());
    }

}
