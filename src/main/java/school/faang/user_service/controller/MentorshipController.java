package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.servis.MentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {
    private MentorshipService mentorshipServis;

    public List<UserDto> getMentes(long userId) {
        return mentorshipServis.getMentees(userId);
    }

    public List<UserDto> getMentors(long userId) {
        return mentorshipServis.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipServis.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipServis.deleteMentor(menteeId, mentorId);
    }
}
