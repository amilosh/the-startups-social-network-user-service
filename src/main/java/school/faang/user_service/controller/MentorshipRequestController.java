package school.faang.user_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

@RestController
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController {

    private final MentorshipRequestService MENTORSHIP_REQUEST_SERVICE;

    @Autowired
    public MentorshipRequestController(MentorshipRequestService mentorshipRequestService) {
        this.MENTORSHIP_REQUEST_SERVICE = mentorshipRequestService;
    }

    @PostMapping("/requestMentorship")
    public ResponseEntity<Void> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        MENTORSHIP_REQUEST_SERVICE.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/acceptMentorship")
    public ResponseEntity<Void> acceptMentorship(@RequestBody Long id) {
        MENTORSHIP_REQUEST_SERVICE.acceptMentorship(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rejectMentorship")
    public ResponseEntity<Void> rejectRequest(long id, RejectionDto rejection) {
        MENTORSHIP_REQUEST_SERVICE.rejectRequest(id, rejection);
        return ResponseEntity.ok().build();
    }

}
