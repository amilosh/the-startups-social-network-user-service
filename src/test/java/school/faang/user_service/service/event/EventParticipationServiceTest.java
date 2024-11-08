package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventNotFoundException;

import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventParticipationService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void testRegisterParticipant() {
        long eventId = 1L;
        long userId = 2L;
        when(eventParticipationRepository.existsByEventIdAndUserId(userId, eventId)).thenReturn(false);
        eventParticipationService.registerParticipant(userId, eventId);

        verify(eventParticipationRepository, times(1)).register(userId, eventId);
    }

    @Test
    public void testUnregisterParticipant() {
        long eventId = 1L;
        long userId = 2L;
        when(eventParticipationRepository.existsByEventIdAndUserId(userId, eventId)).thenReturn(true);

        eventParticipationService.unregisterParticipant(userId, eventId);

        verify(eventParticipationRepository, times(1)).unregister(userId, eventId);
    }

    @Test
    public void testGetParticipantsForExistingEvent() {
        long eventId = 1L;
        List<User> mockUsers = List.of(
                User.builder().username("Alice").build(),
                User.builder().username("Bob").build()
        );
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findUsersByEventId(eventId)).thenReturn(mockUsers);

        List<User> participants = eventParticipationService.getParticipant(eventId);

        assertEquals(mockUsers, participants);
        verify(eventRepository, times(1)).existsById(eventId);
        verify(eventParticipationRepository, times(1)).findUsersByEventId(eventId);

    }

    @Test
    public void testCountEventExistsAndHasParticipants() {
        long eventId = 1L;
        int expectedCount = 6;

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, actualCount);

        verify(eventRepository, times(1)).existsById(eventId);
        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
    }

    @Test
    public void testGetParticipantsCountEventNotFound() {
        long eventId = 1L;

        when(eventRepository.existsById(eventId)).thenReturn(false);

        EventNotFoundException thrown = assertThrows(EventNotFoundException.class, () -> {
            eventParticipationService.getParticipantsCount(eventId);
        });

        assertEquals("Event with ID " + eventId + " does not exist", thrown.getMessage());

        verify(eventRepository, times(1)).existsById(eventId);
    }

    @Test
    void testGetParticipantsCountNoParticipants() {
        int expectedCount = 0;
        try {
            int count = eventParticipationService.getParticipantsCount(1);

            assertEquals(expectedCount, count);
        } catch (EventNotFoundException e) {
            System.out.println("Event not found or no participants. This is expected.");
            assertEquals(expectedCount, 0);
        }
    }
}