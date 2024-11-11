package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshiprequest.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.service.abstracts.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto requestMentorship(@Valid MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        return mentorshipRequestService.getRequests(requestFilterDto);
    }

    public MentorshipRequestDto acceptRequest(Long requestId) {
        return mentorshipRequestService.acceptRequest(requestId);
    }

    public MentorshipRequestDto rejectRequest(Long requestId, @Valid RejectionDto rejectionDto) {
        return mentorshipRequestService.rejectRequest(requestId, rejectionDto);
    }
}