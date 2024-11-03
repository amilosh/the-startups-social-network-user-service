package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.REQUEST)
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping(UrlUtils.ID + UrlUtils.CREATE_REQUEST)
    public ResponseEntity<Void> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.description().length() > 100) {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(UrlUtils.REQUESTS_FILTER)
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@RequestBody RequestFilterDto filter) {
        return ResponseEntity.ok(mentorshipRequestService.getRequest(filter));
    }

    @PutMapping(UrlUtils.ID + UrlUtils.ACCEPT)
    public ResponseEntity<Void> acceptRequest(@PathVariable("id") @Min(1) Long requestId) {
        mentorshipRequestService.acceptRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(UrlUtils.ID + UrlUtils.REJECT)
    public ResponseEntity<Void> rejectRequest(@PathVariable("id") @Min(1) long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection.reason());
        return ResponseEntity.ok().build();
    }
}