package school.faang.user_service.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipService {
    private final UserMapper userMapper;
    private final UserService userService;

    public List<UserDto> getMentees(long userId) {
        User user = userService.findUser(userId);
        List<User> mentees = user.getMentees() != null ? user.getMentees() : new ArrayList<>();
        return userMapper.toDto(mentees);
    }

    public List<UserDto> getMentors(long userId) {
        User user = userService.findUser(userId);
        List<User> mentors = user.getMentors() != null ? user.getMentors() : new ArrayList<>();
        return userMapper.toDto(mentors);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userService.findUser(mentorId);
        mentor.getMentees().stream()
                .filter(m -> m.getId() == menteeId)
                .findFirst()
                .ifPresentOrElse(userService::deleteUser, () -> {
                    System.out.println("Mentor " + mentor.getUsername() +
                            " does not have a mentee with " + menteeId + " id");
                });
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userService.findUser(menteeId);
        mentee.getMentors().stream()
                .filter(m -> m.getId() == mentorId)
                .findFirst()
                .ifPresentOrElse(userService::deleteUser, () -> {
                    System.out.println("User " + mentee.getUsername()
                            + " does not have a mentor with " + mentorId + " id");
                });
    }
}

