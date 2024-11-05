package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RequestMapping("/mentorship")
@RestController
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @Operation (
            summary = "получение всех ментис",
            description = "бла бла бла"
    )
    @GetMapping("/mentees/{mentorId}")
    public List<UserDto> getMentees(long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    public List<UserDto> getMentors(long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
