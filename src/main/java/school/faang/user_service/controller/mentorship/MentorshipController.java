package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(long userId) {
      return mentorshipService.getMentees(userId);
    }
}
