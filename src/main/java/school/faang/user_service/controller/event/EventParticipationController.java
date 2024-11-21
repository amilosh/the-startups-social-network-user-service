package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.ParticipantReqParam;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + UrlUtils.ID + UrlUtils.PARTICIPANTS)
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping()
    public void registerParticipant(@PathVariable("id") @Min(1) Long eventId, @Valid @RequestBody ParticipantReqParam participantReqParam) {
        eventParticipationService.registerParticipant(eventId, participantReqParam.participantId());
    }

    @DeleteMapping()
    public void unregisterParticipant(@PathVariable("id") @Min(1) Long eventId, @Valid @RequestBody ParticipantReqParam participantReqParam) {
        eventParticipationService.unregisterParticipant(eventId, participantReqParam.participantId());
    }

    @GetMapping()
    public List<UserDto> getParticipant(@PathVariable("id") @Min(1) Long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping(UrlUtils.AMOUNT)
    public Long getParticipantsCount(@PathVariable("id") @Min(1) Long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
