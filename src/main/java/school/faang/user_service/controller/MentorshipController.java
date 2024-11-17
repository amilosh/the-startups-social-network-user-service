package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final UserValidator userValidator;

    @GetMapping("/{userId}/mentees")
    public List<UserDto> getMentees(@PathVariable long userId) {
        userValidator.validateUserId(userId);
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    public List<UserDto> getMentors(@PathVariable long userId) {
        userValidator.validateUserId(userId);
        return mentorshipService.getMentors(userId);
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
