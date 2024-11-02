package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

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
    public void testRegisterParticipant_UserRegistered(){

    }
}
