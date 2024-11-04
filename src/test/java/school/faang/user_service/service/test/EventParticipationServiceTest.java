package school.faang.user_service.service.test;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.repository.EventParticipationRepository;
import school.faang.user_service.service.eventService.EventParticipationService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Тест для регистрации участника
    @Test
    public void testRegisterParticipation_Success() {
        long eventId = 1L;
        long userId = 2L;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(0);
        eventParticipationService.registerParticipation(eventId, userId);
        verify(eventParticipationRepository, times(1)).register(eventId, userId);

    }

    @Test
    public void testRegisterParticipation_UserAlreadyRegistered() {
        long eventId = 1L;
        long userId = 1L;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(1);
        Exception exception = assertThrows(RuntimeException.class, () -> eventParticipationService.
                registerParticipation(eventId, userId));
        assertEquals("Пользователь уже зарегистрирован на событие", exception.getMessage());
    }

    // Тест для отмены регистрации
    @Test
    public void testUnregisterParticipation_Success() {
        long eventId = 1L;
        long userId = 2L;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(1);
        eventParticipationService.unregisterParticipation(eventId, userId);
        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    public void testUnregisterParticipation_UserNorRegistered() {
        long eventId = 1L;
        long userId = 2L;
        Exception exception = assertThrows(RuntimeException.class, () ->
                eventParticipationService.unregisterParticipation(eventId, userId));
        assertEquals("Пользователь не зарегисрирован на событие ", exception.getMessage());
    }

    @Test
    public void testGetParticipantCount () {
        long eventId = 1L;
        int expectedCount = 5;

        Mockito.when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, actualCount);

        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
    }

}
