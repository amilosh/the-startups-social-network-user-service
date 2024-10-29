package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(@Valid EventDto eventDto) {
        return eventService.create(eventDto);
    }

    public EventDto getEvent(@NotNull Long eventId) {
        return eventService.getEvent(eventId);
    }

    public EventDto deleteEvent(@NotNull Long eventId) {
        return eventService.deleteEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public EventDto updateEvent(@Valid EventDto event) {
        return eventService.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(@NotNull Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder strBuilder = new StringBuilder();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName;
            try {
                fieldName = ((FieldError) error).getField();

            } catch (ClassCastException ex) {
                fieldName = error.getObjectName();
            }
            String message = error.getDefaultMessage();
            strBuilder.append(String.format("%s: %s\n", fieldName, message));
        });
        log.info(String.valueOf(strBuilder));
    }
}