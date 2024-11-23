package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestStatusDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.MENTORSHIP + UrlUtils.REQUEST)
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;
    private static final int LENGTH_DESCRIPTION = 35;

    @PostMapping
    public ResponseEntity<Void> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        checkLengthDescription(mentorshipRequestDto);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@RequestParam(required = false) String description,
                                                                  @RequestParam(required = false) Long requesterId,
                                                                  @RequestParam(required = false) Long receiverId,
                                                                  @RequestParam(required = false) RequestStatusDto status) {
        return ResponseEntity.ok(mentorshipRequestService.getRequest(description, requesterId, receiverId, status));
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

    private void checkLengthDescription(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.description().isBlank()) {
            log.error("Field Description for request is blank");
            throw new IllegalArgumentException("Field Description for request is blank");
        }
        if (mentorshipRequestDto.description().length() < LENGTH_DESCRIPTION) {
            log.error("Description for request less " + LENGTH_DESCRIPTION + "symbols");
            throw new IllegalArgumentException(String.format("Description for request less %s symbols", LENGTH_DESCRIPTION));
        }
    }
}