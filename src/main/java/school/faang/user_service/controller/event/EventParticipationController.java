package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipant(@NonNull @Valid EventDto eventDto, @NonNull @Valid UserDto userDto) {
        eventParticipationService.registerParticipant(eventDto.getId(), userDto.getId());
    }

    public void unregisterParticipant(@NonNull @Valid EventDto eventDto, @NonNull @Valid UserDto userDto) {
        eventParticipationService.unregisterParticipant(eventDto.getId(), userDto.getId());
    }

    public List<User> getParticipant(@NonNull @Valid EventDto eventDto) {
        return eventParticipationService.getParticipant(eventDto.getId());
    }

    public int getParticipantsCount(@NonNull @Valid EventDto eventDto) {
        return eventParticipationService.getParticipantsCount(eventDto.getId());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationExceptions(@NotNull MethodArgumentNotValidException ex) {
        throw new IllegalArgumentException("Validation failed", ex);
    }
}
