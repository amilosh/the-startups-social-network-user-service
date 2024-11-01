package school.faang.user_service.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request-mentorship")
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<MentorshipRequest>> getRequests(@Valid @RequestBody RequestFilterDto requestFilterDto) {
        List<MentorshipRequest> mentorshipRequestDtos = mentorshipRequestService.getRequests(requestFilterDto);
        return ResponseEntity.ok(mentorshipRequestDtos);
    }

    @PostMapping("/{id}accept")
    public ResponseEntity<Void> acceptMentorship(@PathVariable Long id) {
        mentorshipRequestService.acceptMentorship(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<MentorshipRequestDto> rejectRequest(@Valid @PathVariable long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok().build();
    }
}
