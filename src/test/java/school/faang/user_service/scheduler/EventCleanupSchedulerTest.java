package school.faang.user_service.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.EventService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class EventCleanupSchedulerTest {

    @InjectMocks
    private EventCleanupScheduler eventCleanupScheduler;

    @Mock
    private EventService eventService;

    @Test
    @DisplayName("Test clear events")
    void testClearEvents() {
        eventCleanupScheduler.clearEvents();

        verify(eventService, times(1)).deleteCompletedAndCanceledEvents();
    }
}

