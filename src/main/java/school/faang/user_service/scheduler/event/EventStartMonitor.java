package school.faang.user_service.scheduler.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
public class EventStartMonitor {

    private final EventService eventService;

    @Scheduled(cron = "${app.event.start.cron}")
    public void monitorEvents() {
        eventService.findEventsStartingNow();
    }
}
