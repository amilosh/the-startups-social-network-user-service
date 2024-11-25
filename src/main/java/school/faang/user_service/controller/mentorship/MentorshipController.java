package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/{Id}/mentees/get")
    public List<UserDto> getMentees(@PathVariable("Id") long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{Id}/mentors/get")
    public List<UserDto> getMentors(@PathVariable("Id") long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/{Id}/mentees/{menteeId}")
    public void deleteMentee(@PathVariable long menteeId, @PathVariable("Id") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/{Id}/mentors/{mentorId}")
    public void deleteMentor(@PathVariable("Id") long menteeId, @PathVariable long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
