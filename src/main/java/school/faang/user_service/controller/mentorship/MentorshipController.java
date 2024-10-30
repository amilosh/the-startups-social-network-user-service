package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(long menteeId) {
        return mentorshipService.getMentees(menteeId);
    }

    public List<UserDto> getMentors(long mentorId) {
        return mentorshipService.getMentors(mentorId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
