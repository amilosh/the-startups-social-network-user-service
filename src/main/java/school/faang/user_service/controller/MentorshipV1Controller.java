package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.MenteeResponseDto;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@RestController("/api/v1/mentorships")
@Validated
@RequiredArgsConstructor
public class MentorshipV1Controller {
    private final MentorshipService mentorshipService;
    private final UserValidator userValidator;

    public List<MenteeResponseDto> getMentees(long userId) {
        userValidator.validateUserId(userId);
        return mentorshipService.getMentees(userId);
    }

    public List<MenteeResponseDto> getMentors(long userId) {
        userValidator.validateUserId(userId);
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        userValidator.validateUserId(menteeId);
        userValidator.validateUserId(mentorId);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        userValidator.validateUserId(menteeId);
        userValidator.validateUserId(mentorId);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
