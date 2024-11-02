package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.UserAlreadyRegisteredException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

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
    @DisplayName("Проверка \"Пользователь зарегистрировался\"")
    public void testRegisterParticipant_UserRegistered() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(new ArrayList<>());

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).register(eventId, userId);
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
    }

    @Test
    @DisplayName("Проверка \"Пользователь уже зарегистрирован\"")
    public void testRegisterParticipant_UserAlreadyRegistered() {
        List<User> participants = List.of(testUser);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        assertThrows(UserAlreadyRegisteredException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });

        verify(eventParticipationRepository, never()).register(eventId, userId);
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
    }

    @Test
    @DisplayName("Проверка \"Пользователь отменил регистрацию\"")
    public void testUnregisterParticipant_UserUnregistered() {

    }

    @Test
    @DisplayName("Проверка \"Пользователь не зарегистрирован\"")
    public void testUnregisterParticipant_UserIsNotRegistered() {

    }
}
