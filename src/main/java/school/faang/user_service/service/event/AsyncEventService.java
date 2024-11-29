package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncEventService {
    private final EventRepository eventRepository;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Void> deleteBatchAsync(List<Long> batch) {
        log.info("Deleting a batch of {} events", batch.size());
        eventRepository.deleteAllById(batch);
        log.info("Events successfully deleted");
        return CompletableFuture.completedFuture(null);
    }
}
