package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipant(@Valid UserDto userDto, @Valid EventDto eventDto) throws Exception {
        eventParticipationService.registerParticipant(userDto, eventDto);
    }

    public void unregisterParticipant(@Valid UserDto userDto, @Valid EventDto eventDto) throws Exception {
        eventParticipationService.unregisterParticipant(userDto, eventDto);
    }

    public List<UserDto> getParticipant(@Valid EventDto eventDto) throws Exception {
        return eventParticipationService.getParticipant(eventDto);
    }

    public long getParticipantsCount(@Valid EventDto eventDto) throws Exception {
        return eventParticipationService.getParticipationCount(eventDto);
    }
}
