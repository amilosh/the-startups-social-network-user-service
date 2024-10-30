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
        validateEvent(event);
        return eventService.create(event);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto event) {
        validateEvent(event);
        return eventService.updateEvent(event);
    }

    private void validateEvent(EventDto event) {
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