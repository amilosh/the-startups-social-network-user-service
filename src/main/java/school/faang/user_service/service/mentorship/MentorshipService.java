package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validation.Validation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;
    private final Validation validation;

    public List<UserDto> getMentees(long userId) {
        User user = validation.validateUserData(userId);
        return user.getMentees().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(long userId) {
        User user = validation.validateUserData(userId);
        return user.getMentors().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = validation.validateUserData(mentorId);
        mentor.getMentees().removeIf(user -> user.getId() == menteeId);
        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        User mentee = validation.validateUserData(menteeId);
        mentee.getMentors().removeIf(user -> user.getId() == mentorId);
        mentorshipRepository.save(mentee);
    }
}
