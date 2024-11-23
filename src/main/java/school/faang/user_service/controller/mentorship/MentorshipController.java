package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/{userId}/mentees")
    public List<UserDto> getMentees(@PathVariable long userId) {
        System.out.println(12321321);
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    public List<UserDto> getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }

    @PutMapping("/{userId}/mentees/{menteeId}")
    public void deleteMentee(@PathVariable long userId, @PathVariable long menteeId) {
        mentorshipService.deleteMentee(userId, menteeId);
    }

    @PutMapping("/{userId}/mentors/{mentorId}")
    public void deleteMentor(@PathVariable long userId, @PathVariable long mentorId) {
        mentorshipService.deleteMentor(userId, mentorId);
    }
}
