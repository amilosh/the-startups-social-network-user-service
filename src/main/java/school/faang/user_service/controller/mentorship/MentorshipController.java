package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validation.Validation;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final Validation validation;

    public List<UserDto> getMentees(long userId) {
        validation.validateIdCorrect(userId);
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(long userId) {
        validation.validateIdCorrect(userId);
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        validation.validateIdCorrect(menteeId);
        validation.validateIdCorrect(mentorId);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        validation.validateIdCorrect(menteeId);
        validation.validateIdCorrect(mentorId);
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
