package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> registerParticipant(@RequestBody @Valid UserDto userDto,
                                                      @RequestBody @Valid EventDto eventDto) {
        eventParticipationService.registerParticipant(userDto, eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User successfully registered for the event.");
    }

    @PostMapping("/unregister")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> unregisterParticipant(@RequestBody @Valid UserDto userDto,
                                                        @RequestBody @Valid EventDto eventDto) {
        eventParticipationService.unregisterParticipant(userDto, eventDto);
        return ResponseEntity.ok("User successfully unregistered from the event.");
    }

    @GetMapping("/participants")
    public ResponseEntity<List<UserDto>> getParticipants(@RequestBody @Valid EventDto eventDto) {
        List<UserDto> participants = eventParticipationService.getParticipants(eventDto);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/participants/count")
    public ResponseEntity<Integer> getParticipantsCount(@RequestBody @Valid EventDto eventDto) {
        int count = eventParticipationService.getParticipantsCount(eventDto);
        return ResponseEntity.ok(count);
    }
}
