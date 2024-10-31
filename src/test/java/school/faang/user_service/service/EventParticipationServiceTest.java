package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService service;

    @Mock
    private EventParticipationRepository repository;

    private long eventId;
    private long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventId = 1L;
        userId = 100L;
    }

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

    private void prepareData() {
        User user = new User();
        user.setId(userId);
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
    }

}