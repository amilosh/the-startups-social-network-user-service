package school.faang.user_service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.EventService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {
    @InjectMocks
    private Scheduler scheduler;
    @Mock
    private EventService eventService;

    @Test
    void testSchedulerRemover() {
        scheduler.clearEvents();
        verify(eventService).deletePastEvents();
    }
}