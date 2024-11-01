package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mentorship-request")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @GetMapping
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping
    public MentorshipRequestDto createRequestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.createRequestMentorship(mentorshipRequestDto);
    }

    @PutMapping("/{id}/accept")
    public MentorshipRequestDto acceptRequest(@PathVariable long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/{id}/reject")
    public MentorshipRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
