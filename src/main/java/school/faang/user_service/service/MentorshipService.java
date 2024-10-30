package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipService {
    private UserRepository userRepository;
    private UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        return userRepository.findById(userId)
                .map(user -> userMapper.toDto(user.getMentees()))
                .orElseGet(ArrayList::new);
    }

    public List<UserDto> getMentors(long userId) {
        return userRepository.findById(userId)
                .map(user -> userMapper.toDto(user.getMentors()))
                .orElseGet(ArrayList::new);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userIsPresent(mentorId);
        if (mentor!=null) {
            mentor.getMentees().stream()
                    .filter(m -> m.getId() == menteeId)
                    .findFirst()
                    .ifPresentOrElse(userRepository::delete, () -> {
                        System.out.println("Mentor " + mentor.getUsername() +
                                " does not have a mentee with " + menteeId + " id");
                    });
        } else {
            System.out.println("Mentor with ID " + mentorId + " not found");
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userIsPresent(menteeId);
        if (mentee != null) {
            mentee.getMentors().stream()
                    .filter(m -> m.getId() == mentorId)
                    .findFirst()
                    .ifPresentOrElse(userRepository::delete, () -> {
                        System.out.println("User " + mentee.getUsername()
                                + " does not have a mentor with " + mentorId + " id");
                    });
        } else {
            System.out.println("Mentee with ID " + menteeId + " not found");
        }
    }

    private User userIsPresent(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }
}

