package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        validateByTitleOwnerIdStartDate(event);
        return eventService.create(event);
    }

    public EventDto get(long eventId) {
        return eventService.get(eventId);
    }

    public List<EventDto> getByFilter(EventFilterDto filter) {
        return eventService.getByFilter(filter);
    }

    public void delete(long eventId) {
        eventService.delete(eventId);
    }

    public EventDto update(EventDto event) {
        validateByTitleOwnerIdStartDate(event);
        return eventService.update(event);
    }

    private void validateByTitleOwnerIdStartDate(EventDto event) {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Event name is required!");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("Event owner id is required");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("Event start date is required");
        }
    }

    private List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    private List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}