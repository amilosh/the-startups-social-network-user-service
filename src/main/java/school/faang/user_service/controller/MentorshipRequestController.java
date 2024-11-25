package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshiprequest.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.service.abstracts.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController implements MentorshipRequestControllerOas {
    private final MentorshipRequestService mentorshipRequestService;

    @Override
    @PostMapping
    public MentorshipRequestDto requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @Override
    @PostMapping("/filter")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto requestFilterDto) {
        return mentorshipRequestService.getRequests(requestFilterDto);
    }

    @Override
    @PatchMapping("/{requestId}/accept")
    public MentorshipRequestDto acceptRequest(@PathVariable long requestId) {
        return mentorshipRequestService.acceptRequest(requestId);
    }

    @Override
    @PatchMapping("/{requestId}/reject")
    public MentorshipRequestDto rejectRequest(@PathVariable long requestId,
                                              @RequestBody @Valid RejectionDto rejectionDto) {
        return mentorshipRequestService.rejectRequest(requestId, rejectionDto);
    }
}
