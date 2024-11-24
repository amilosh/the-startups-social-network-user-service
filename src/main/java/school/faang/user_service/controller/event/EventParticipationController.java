package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eventParticipations")
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping
    public void registerParticipant(@RequestBody @Valid EventDto eventDto, @RequestBody @Valid UserDto userDto) {
        eventParticipationService.registerParticipant(eventDto.getId(), userDto.getId());
    }

    @DeleteMapping
    public void unregisterParticipant(@RequestBody @Valid EventDto eventDto, @RequestBody @Valid UserDto userDto) {
        eventParticipationService.unregisterParticipant(eventDto.getId(), userDto.getId());
    }

    @GetMapping("/users")
    public List<User> getParticipant(@RequestBody @Valid EventDto eventDto) {
        return eventParticipationService.getParticipant(eventDto.getId());
    }

    @GetMapping("/count")
    public int getParticipantsCount(@RequestBody @Valid EventDto eventDto) {
        return eventParticipationService.getParticipantsCount(eventDto.getId());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    //@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public void handleValidationExceptions(@NotNull MethodArgumentNotValidException ex) {
        throw new IllegalArgumentException("Validation failed", ex);
    }
}
