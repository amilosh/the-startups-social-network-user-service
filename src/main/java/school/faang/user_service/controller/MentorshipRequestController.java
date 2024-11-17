package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mentorship-request")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @GetMapping
    public List<MentorshipRequestDto> getRequests(@RequestBody MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDto createRequestMentorship(@Validated @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.createRequestMentorship(mentorshipRequestDto);
    }

    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MentorshipRequestDto acceptRequest(@PathVariable long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/{id}/reject")
    public MentorshipRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
