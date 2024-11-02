package school.faang.user_service.controller.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Component
public class MentorshipController {
    private MentorshipService mentorshipService;

    public List<UserDto> getMentees(long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
