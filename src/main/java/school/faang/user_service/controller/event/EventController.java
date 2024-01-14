package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        if (!isValidate(event)) {
            throw new DataValidationException("Event isn't validate.");
        }
        return eventService.create(event);
    }

    public List<Event> getAllEventsByUserId(long userId) {
        return eventService.getAllEventsByUserId(userId);
    }

    public boolean isValidate(EventDto event) {
        return (event.getTitle() != null && !event.getTitle().isEmpty())
                && event.getStartDate().isAfter(LocalDateTime.now())
                && event.getOwnerId() >= 0;
    }

}
