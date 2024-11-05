package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

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
    @DisplayName("Проверка registerParticipant")
    public void testRegisterParticipant() {
        eventParticipationController.registerParticipant(eventId, userId);

        verify(eventParticipationService, times(1)).registerParticipant(eventId, userId);
    }

    @Test
    @DisplayName("Проверка unregisterParticipant")
    public void testUnregisterParticipant() {
        eventParticipationController.unregisterParticipant(eventId, userId);

        verify(eventParticipationService, times(1)).unregisterParticipant(eventId, userId);
    }

    @Test
    @DisplayName("Проверка getParticipant возвращает список участников")
    public void testGetParticipant() {
        List<User> participants = List.of(testUser);
        when(eventParticipationService.getParticipant(eventId)).thenReturn(participants);

        List<User> result = eventParticipationController.getParticipant(eventId, userId);

        assertEquals(participants, result);
        verify(eventParticipationService, times(1)).getParticipant(eventId);
    }

    @Test
    @DisplayName("Проверка getParticipantsCount возвращает количество участников")
    public void testGetParticipantsCount() {
        int expectedCount = 5;
        when(eventParticipationService.getParticipantsCount(eventId)).thenReturn(expectedCount);

        int result = eventParticipationController.getParticipantsCount(eventId, userId);

        assertEquals(expectedCount, result);
        verify(eventParticipationService, times(1)).getParticipantsCount(eventId);
    }
}
