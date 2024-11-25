package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    private long eventId;
    private long userId;
    private User testUser;

    @BeforeEach
    public void setUp() {
        eventId = 10L;
        userId = 1L;
        testUser = new User();
        testUser.setId(userId);
    }

    @Test
    @DisplayName("Проверка register \"Пользователь зарегистрировался\"")
    public void testRegisterParticipant_UserRegistered() {
        when(eventParticipationRepository
                .findAllParticipantsByEventId(eventId))
                .thenReturn(new ArrayList<>());

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .register(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .findAllParticipantsByEventId(eventId);
    }

    @Test
    @DisplayName("Проверка register \"Пользователь уже зарегистрирован\"")
    public void testRegisterParticipant_UserAlreadyRegistered() {
        List<User> participants = List.of(testUser);
        when(eventParticipationRepository
                .findAllParticipantsByEventId(eventId))
                .thenReturn(participants);

        assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });

        verify(eventParticipationRepository, never())
                .register(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .findAllParticipantsByEventId(eventId);
    }

    @Test
    @DisplayName("Проверка unregister \"Пользователь отменил регистрацию\"")
    public void testUnregisterParticipant_UserUnregistered() {
        List<User> participants = List.of(testUser);
        when(eventParticipationRepository
                .findAllParticipantsByEventId(eventId))
                .thenReturn(participants);

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .unregister(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .findAllParticipantsByEventId(eventId);
    }

    @Test
    @DisplayName("Проверка unregister \"Пользователь не зарегистрирован\"")
    public void testUnregisterParticipant_UserIsNotRegistered() {
        when(eventParticipationRepository
                .findAllParticipantsByEventId(eventId))
                .thenReturn(new ArrayList<>());

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });

        verify(eventParticipationRepository, never())
                .unregister(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .findAllParticipantsByEventId(eventId);
    }

    @Test
    @DisplayName("Проверка getParticipant")
    public void testGetParticipant_ShouldReturnParticipantsList() {
        List<User> participants = List.of(testUser);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        List<User> result = eventParticipationService.getParticipant(eventId);

        assertEquals(participants, result);

        verify(eventParticipationRepository, times(1))
                .findAllParticipantsByEventId(eventId);
    }

    @Test
    @DisplayName("Проверка getParticipantsCount")
    public void testGetParticipantsCount_ShouldReturnCount() {
        int expectedCount = 5;
        when(eventParticipationRepository
                .countParticipants(eventId))
                .thenReturn(expectedCount);

        int result = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, result);

        verify(eventParticipationRepository, times(1))
                .countParticipants(eventId);
    }
}
