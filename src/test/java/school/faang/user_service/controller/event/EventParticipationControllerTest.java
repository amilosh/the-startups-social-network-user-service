package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.EventParticipationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {

    private final long EVENT_ID = 1L;
    private final long USER_ID = 1L;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Mock
    private EventParticipationService eventParticipationService;

    @Test
    void testRegisterParticipant() {
        eventParticipationController.registerParticipant(EVENT_ID, USER_ID);
        verify(eventParticipationService, times(1)).registerParticipant(EVENT_ID, USER_ID);
    }

    @Test
    void testUnregisterParticipant() {
        eventParticipationController.unregisterParticipant(EVENT_ID, USER_ID);
        verify(eventParticipationService, times(1)).unregisterParticipant(EVENT_ID, USER_ID);
    }

    @Test
    void testGetParticipant() {
        eventParticipationController.getParticipant(EVENT_ID);
        verify(eventParticipationService, times(1)).getParticipant(EVENT_ID);
    }

    @Test
    void testGetParticipantCount() {
        eventParticipationController.getParticipantCount(EVENT_ID);
        verify(eventParticipationService, times(1)).getParticipantsCount(EVENT_ID);
    }
}