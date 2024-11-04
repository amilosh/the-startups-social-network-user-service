package school.faang.user_service.service.test;
import org.junit.jupiter.api.Test; // Импортируйте из JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.repository.EventParticipationRepository;
import school.faang.user_service.service.EventParticipationValidator;
import school.faang.user_service.service.eventService.EventParticipationService;
import school.faang.user_service.service.userService.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventParticipationValidator validator;

    @Mock
    private UserService userService;

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
