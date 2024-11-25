package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventScheduler {
    private final EventService eventService;

    @Scheduled(cron = "${clear-events.scheduler.cron}")
    public void clearEvents() {
        log.info("Starting scheduled task to clear past events");
        eventService.clearPastEvents();
        log.info("Scheduled task to clear past events completed successfully");
    }
}
