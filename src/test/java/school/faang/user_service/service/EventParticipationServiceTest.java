package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.EventParticipationRepository;
import school.faang.user_service.service.validator.event.EventParticipationValidator;
import school.faang.user_service.service.event.EventParticipationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventParticipationValidator validator;
    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void testRegisterParticipation_Success() {
        long eventId = 1L;
        long userId = 2L;

        // Настройка моков для валидации
        doNothing().when(validator).validateParticipation(eventId, userId); // Предполагаем, что метод ничего не возвращает
        doNothing().when(validator).validateRegisterParticipation(eventId, userId); // Настройка на отсутствие исключений
        doNothing().when(eventParticipationRepository).register(eventId, userId);

        eventParticipationService.registerParticipation(eventId, userId);


        verify(validator).validateParticipation(eventId, userId);
        verify(validator).validateRegisterParticipation(eventId, userId);
        verify(eventParticipationRepository).register(eventId, userId);
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
        assertEquals("Пользователь не зарегисрирован на событие", exception.getMessage());
    }

    @Test
    public void testGetParticipantCount() {
        long eventId = 1L;
        int expectedCount = 5;

        Mockito.when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, actualCount);

        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
    }

    @Test
    public void testRegisterParticipation_NullEventId() {
        long userId = 2L;
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipation(null, userId));
        assertEquals("Event ID cannot be null", exception.getMessage());
    }

    @Test
    public void testRegisterParticipation_NullUserId() {
        long eventId = 1L;
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipation(eventId, null));
        assertEquals("User ID cannot be null", exception.getMessage());
    }

}
