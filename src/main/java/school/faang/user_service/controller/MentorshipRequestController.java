package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping()
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@Valid @RequestBody RequestFilterDto requestFilterDto) {
        List<MentorshipRequestDto> mentorshipRequestDto = mentorshipRequestService.getRequests(requestFilterDto);
        return ResponseEntity.ok(mentorshipRequestDto);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Long> acceptMentorship(@PathVariable Long id) {
        mentorshipRequestService.acceptMentorship(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<MentorshipRequestDto> rejectRequest(@Valid @PathVariable long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok().build();
    }
}
