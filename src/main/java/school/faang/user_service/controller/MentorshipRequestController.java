package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController {
    private final MentorshipRequestService requestService;

    @PostMapping
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@Valid @RequestBody MentorshipRequestDto dto) {
        log.info("Requesting mentorship from userId #{} to userId #{}.", dto.getRequesterId(), dto.getReceiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.requestMentorship(dto));
    }

    @GetMapping("/requests/filtered")
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@Valid @RequestBody RequestFilterDto filters) {
        log.info("Getting all requests by filters: {}.", filters);
        return ResponseEntity.ok().body(requestService.getRequests(filters));
    }

    @PutMapping("/requests/accepts/{id}")
    public ResponseEntity<MentorshipRequestDto> acceptRequest(@PathVariable @NotNull @Positive Long id) {
        log.info("Accepting request with id #{}", id);
        return ResponseEntity.ok().body(requestService.acceptRequest(id));
    }

    @PutMapping("/requests/rejects/{id}")
    public ResponseEntity<MentorshipRequestDto> rejectRequest(@PathVariable @NotNull @Positive Long id,
                                                              @Valid @RequestBody RejectionDto rejectionDto) {
        log.info("Rejecting request with id #{} and reason '{}'", id, rejectionDto.getReason());
        return ResponseEntity.ok().body(requestService.rejectRequest(id, rejectionDto));
    }
}
