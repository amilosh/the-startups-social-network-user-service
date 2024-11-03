package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Tag(name = "MentorshipRequests", description = "API for managing mentorship requests.")
@RestController
@RequestMapping("/mentorship/requests")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestCreationDto creationRequestDto) {
        return mentorshipRequestService.requestMentorship(creationRequestDto);
    }

    @PostMapping("/filter")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filterDto) {
        return mentorshipRequestService.getRequests(filterDto);
    }

    @PatchMapping("/{requestId}/accept")
    public MentorshipRequestDto acceptRequest(@PathVariable Long requestId) {
        return mentorshipRequestService.acceptRequest(requestId);
    }

    @PatchMapping("/{requestId}/reject")
    public MentorshipRequestDto rejectRequest(@PathVariable Long requestId, @Valid @RequestBody RejectionDto rejectionDto) {
        return mentorshipRequestService.rejectRequest(requestId, rejectionDto);
    }
}