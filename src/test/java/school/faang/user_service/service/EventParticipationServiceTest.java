package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService service;

    @Mock
    private EventParticipationRepository repository;

    private final long eventId = 1L;
    private final long userId = 100L;

    @Test
    public void testRegisterParticipantWithRegisteredUser() {
        prepareData();
        assertThrows(IllegalStateException.class, () -> service.registerParticipant(eventId, userId));
    }

    @Test
    public void testRegisterParticipant() {
        service.registerParticipant(eventId, userId);
        verify(repository, times(1)).register(eventId, userId);
    }

    @Test
    void testIsParticipantRegistered() {
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(new ArrayList<>());
        assertThrows(IllegalStateException.class, () -> service.unregisterParticipant(eventId, userId));
    }

    @Test
    void testUnregisterParticipant() {
        prepareData();
        service.unregisterParticipant(eventId, userId);
        verify(repository, times(1)).unregister(eventId, userId);
    }

    private void prepareData() {
        User user = new User();
        user.setId(userId);
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
    }

    @Test
    void testEmptyEvent() {
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(null);
        assertThrows(IllegalStateException.class, () -> service.getParticipant(eventId));
    }

    @Test
    void testGetParticipants() {
        List<User> mockUsers = List.of(mock(User.class));
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(mockUsers);
        assertEquals(mockUsers, service.getParticipant(eventId));
    }

    @Test
    void testGetParticipantsCount() {
        when(repository.countParticipants(eventId)).thenReturn(1);
        assertEquals(1, service.getParticipantsCount(eventId));
        verify(repository, times(1)).countParticipants(eventId);
    }

}