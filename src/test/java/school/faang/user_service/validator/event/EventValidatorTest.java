package school.faang.user_service.validator.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.exception.participation.EventNotFoundException;
import school.faang.user_service.exception.participation.ParticipationException;
import school.faang.user_service.exception.participation.UserNotFoundException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventValidatorTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventValidator eventValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void TestUserExists() {
        long userId = 1L;

        when(eventRepository.existsById(userId)).thenReturn(true);

        eventValidator.validateUserExists(userId);

        verify(eventRepository, times(1)).existsById(userId);
    }

    @Test
    void TestUserNotFound() {
        long userId = 1L;

        when(eventRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> eventValidator.validateUserExists(userId));

        verify(eventRepository, times(1)).existsById(userId);
    }

    @Test
    void TestEventExists() {
        long eventId = 2L;

        when(eventRepository.existsById(eventId)).thenReturn(true);

        eventValidator.validateEventExists(eventId);

        verify(eventRepository, times(1)).existsById(eventId);
    }

    @Test
    void TestEventNotFound() {
        long eventId = 2L;

        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> eventValidator.validateEventExists(eventId));

        verify(eventRepository, times(1)).existsById(eventId);
    }

    @Test
    void TestUserRegistered() {
        long eventId = 3L;
        long userId = 1L;

        when(eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(true);

        eventValidator.validateUserIsRegistered(eventId, userId);

        verify(eventParticipationRepository, times(1)).existsByEventIdAndUserId(eventId, userId);
    }

    @Test
    void TestUserNotRegisteredForEvent() {
        long eventId = 3L;
        long userId = 1L;

        when(eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);

        assertThrows(ParticipationException.class, () -> eventValidator.validateUserIsRegistered(eventId, userId));

        verify(eventParticipationRepository, times(1)).existsByEventIdAndUserId(eventId, userId);
    }

    @Test
    void TestUserNotYetRegistered() {
        long eventId = 3L;
        long userId = 1L;

        when(eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);

        eventValidator.validateUserNotRegistered(eventId, userId);

        verify(eventParticipationRepository, times(1)).existsByEventIdAndUserId(eventId, userId);
    }

    @Test
    void TestUserAlreadyRegistered() {
        long eventId = 3L;
        long userId = 1L;

        when(eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(true);

        assertThrows(ParticipationException.class, () -> eventValidator.validateUserNotRegistered(eventId, userId));

        verify(eventParticipationRepository, times(1)).existsByEventIdAndUserId(eventId, userId);
    }
}

