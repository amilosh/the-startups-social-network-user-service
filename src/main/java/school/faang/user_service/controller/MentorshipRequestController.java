package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mentorship-request")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final Validator mentorshipRequestValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(mentorshipRequestValidator);
    }

    @GetMapping
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping
    public ResponseEntity<Object> createRequestMentorship(
            @Valid @RequestBody MentorshipRequestDto mentorshipRequestDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    bindingResult.getFieldErrors().stream()
                            .map(error -> error.getField() + ": " + error.getDefaultMessage())
                            .toList());
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mentorshipRequestService.createRequestMentorship(mentorshipRequestDto));
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
