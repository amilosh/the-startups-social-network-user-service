package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipant(@Valid UserDto userDto, @Valid EventDto eventDto) {
        eventParticipationService.registerParticipant(userDto, eventDto);
    }

    public void unregisterParticipant(@Valid UserDto userDto, @Valid EventDto eventDto) {
        eventParticipationService.unregisterParticipant(userDto, eventDto);
    }

    public List<UserDto> getParticipants(@Valid EventDto eventDto) {
        return eventParticipationService.getParticipants(eventDto);
    }

    public long getParticipantsCount(@Valid EventDto eventDto) {
        return eventParticipationService.getParticipantsCount(eventDto);
    }
}
