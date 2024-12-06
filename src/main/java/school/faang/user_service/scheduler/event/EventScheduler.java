package school.faang.user_service.scheduler.event;

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

    @Scheduled(cron = "${app.event.cron}")
    public void clearEvents() {
        try {
            eventService.deletePastEvents().get();
        } catch (Exception e) {
            log.error("Error during scheduled event clearing: {}", e.getMessage(), e);
        }
    }
}
