package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCleanerService {

    private final EventRepository eventRepository;

    @Async("cachedThreadPool")
    @Transactional
    public void deleteSelectedListEventsAsync(List<Event> eventList) {
        eventList.forEach(event -> eventRepository.deleteById(event.getId()));
    }
}
