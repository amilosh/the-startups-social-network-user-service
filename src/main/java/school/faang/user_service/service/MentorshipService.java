package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        testValidUserId(userId);

        User user = getUser(userId);
        List<User> mentees = user.getMentees();

        testEmptyValue(mentees);
        return mentees.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getMentors(long userId) {
        testValidUserId(userId);

        User user = getUser(userId);
        List<User> mentors = user.getMentors();

        testEmptyValue(mentors);
        return mentors.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        testValidUserId(menteeId);
        testValidUserId(mentorId);

        User mentor = getUser(mentorId);
        testEmptyValue(mentor.getMentees());
        mentor.getMentees().removeIf(user -> user.getId() == menteeId);
        userRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        testValidUserId(menteeId);
        testValidUserId(mentorId);

        User mentee = getUser(menteeId);
        testEmptyValue(mentee.getMentors());
        mentee.getMentors().removeIf(user -> user.getId() == mentorId);
        userRepository.save(mentee);
    }

    private void testValidUserId(long userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("Не верный id пользователя");
        }
    }

    private void testEmptyValue(List<User> users) {
        if (users == null) {
            throw new IllegalArgumentException("Отсутствует значение");
        }
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("Такого пользователя не существует"));
    }
}
