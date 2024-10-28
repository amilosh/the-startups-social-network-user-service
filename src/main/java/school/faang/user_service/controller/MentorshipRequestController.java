package school.faang.user_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

@RestController
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @Autowired
    public MentorshipRequestController(MentorshipRequestService mentorshipRequestService) {
        this.mentorshipRequestService = mentorshipRequestService;
    }

    @PostMapping("/requestMentorship")
    public ResponseEntity<Void> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/acceptMentorship")
    public ResponseEntity<Void> acceptMentorship(@PathVariable Long id) {
        mentorshipRequestService.acceptMentorship(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rejectMentorship")
    public ResponseEntity<Void> rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok().build();
    }

}
