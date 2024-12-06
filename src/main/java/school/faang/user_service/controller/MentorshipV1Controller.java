package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.MenteeResponseDto;
import school.faang.user_service.service.user.MentorshipService;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mentorships")
@Validated
@RequiredArgsConstructor
public class MentorshipV1Controller {
    private final MentorshipService mentorshipService;
    private final UserValidator userValidator;

    @GetMapping("/{mentorId}/mentees")
    public List<MenteeResponseDto> getMentees(@PathVariable long mentorId) {
        userValidator.validateUserId(mentorId);
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/{menteeId}/mentors")
    public List<MenteeResponseDto> getMentors(@PathVariable long menteeId) {
        userValidator.validateUserId(menteeId);
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/{mentorId}/mentees/{menteeId}")
    public void deleteMentee(@PathVariable long menteeId,
                             @PathVariable long mentorId) {
        userValidator.validateUserId(menteeId);
        userValidator.validateUserId(mentorId);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/{menteeId}/mentors/{mentorId}")
    public void deleteMentor(@PathVariable long menteeId,
                             @PathVariable long mentorId) {
        userValidator.validateUserId(menteeId);
        userValidator.validateUserId(mentorId);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
