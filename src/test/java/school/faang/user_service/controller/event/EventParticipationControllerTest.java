package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Arrays;
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

    @BeforeEach
    void setUp() {
        eventId = 1L;
        userId = 1L;
    }

    @Test
    public void testFindAllParticipantsByEventId() {
        List<UserDto> expectedParticipants = Arrays.asList(new UserDto(), new UserDto());
        when(eventParticipationService.findAllParticipantsByEventId(eventId)).thenReturn(expectedParticipants);

        List<UserDto> actualParticipants = eventParticipationController.findAllParticipantsByEventId(eventId);

        assertEquals(expectedParticipants, actualParticipants);
    }

    @Test
    public void testFindParticipantsAmountByEventId() {
        int expectedAmount = 2;
        when(eventParticipationService.findParticipantsAmountByEventId(eventId)).thenReturn(expectedAmount);

        int actualAmount = eventParticipationController.findParticipantsAmountByEventId(eventId);

        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    public void testRegisterParticipant() {
        eventParticipationController.registerParticipant(eventId, userId);

        verify(eventParticipationService, times(1)).registerParticipant(eventId, userId);
    }

    @Test
    public void testUnregisterParticipant() {
        long eventId = 1L;
        long userId = 1L;

        eventParticipationController.unregisterParticipant(eventId, userId);

        verify(eventParticipationService, times(1)).unregisterParticipant(eventId, userId);
    }
}
