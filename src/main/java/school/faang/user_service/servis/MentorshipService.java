package school.faang.user_service.servis;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipService {
    private UserRepository userRepository;
    private UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return userMapper.toDto(user.get().getMentees());
        }
        return new ArrayList<>();
    }

    public List<UserDto> getMentors(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return userMapper.toDto(user.get().getMentors());
        }
        return new ArrayList<>();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        Optional<User> mentor = userRepository.findById(mentorId);
        if (mentor.isPresent()) {
            mentor.get().getMentees().stream()
                    .filter(m -> m.getId() == menteeId)
                    .findFirst()
                    .ifPresentOrElse(userRepository::delete, () -> {
                        System.out.println("Mentor " + mentor.get().getUsername() +
                                " does not have a mentee with " + menteeId + " id");
                    });
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        Optional<User> mentee = userRepository.findById(menteeId);
        if (mentee.isPresent()) {
            mentee.get().getMentors().stream()
                    .filter(m -> m.getId() == mentorId)
                    .findFirst()
                    .ifPresentOrElse(userRepository::delete, () -> {
                        System.out.println("User " + mentee.get().getUsername()
                                + " does not have a mentor with " + mentorId + " id");
                    });
        }
    }
}
