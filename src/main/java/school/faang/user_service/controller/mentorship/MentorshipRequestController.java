package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("mentorship/request")
@RequiredArgsConstructor
@Validated
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;


    @GetMapping
    public List<MentorshipRequestDto> getRequests(@Valid @ModelAttribute RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping(path = "accept")
    public MentorshipRequestDto acceptRequest(@Valid @RequestParam long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping(path = "reject")
    public MentorshipRequestDto rejectRequest(@Valid @RequestParam long id, @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }

    @PostMapping
    public MentorshipRequestDto requestMentorship( @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
