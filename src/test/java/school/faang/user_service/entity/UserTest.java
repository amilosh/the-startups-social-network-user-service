package school.faang.user_service.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserTest {
    @Mock
    private Event mockEvent;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setParticipatedEvents(new ArrayList<>());
        user.setOwnedEvents(new ArrayList<>());
    }

    @Test
    void testRemoveParticipatedEvent() {
        user.getParticipatedEvents().add(mockEvent);
        assertTrue(user.getParticipatedEvents().contains(mockEvent));

        user.removeParticipatedEvent(mockEvent);

        assertFalse(user.getParticipatedEvents().contains(mockEvent));
    }

    @Test
    void testRemoveOwnedEvent() {
        user.getOwnedEvents().add(mockEvent);
        assertTrue(user.getOwnedEvents().contains(mockEvent));

        user.removeOwnedEvent(mockEvent);

        assertFalse(user.getOwnedEvents().contains(mockEvent));
    }

    @Test
    void testToLogString() {
        user.setUsername("testUser");
        user.setId(1L);

        String logString = user.toLogString();
        assertEquals("User testUser with id 1", logString);
    }
}

