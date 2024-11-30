package school.faang.user_service.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "test-logs.scheduler.enabled", havingValue = "true")
public class LogsScheduler {

    @Scheduled(fixedRate = 200)
    public void logDebugMessage() {
        log.debug("DEBUG message from LogsScheduler");
    }

    @Scheduled(fixedRate = 300)
    public void logInfoMessage() {
        log.info("INFO message from LogsScheduler");
    }

    @Scheduled(fixedRate = 500)
    public void logWarnMessage() {
        log.warn("WARN message from LogsScheduler");
    }

    @Scheduled(fixedRate = 700)
    public void logErrorMessage() {
        log.error("ERROR message from LogsScheduler");
    }
}