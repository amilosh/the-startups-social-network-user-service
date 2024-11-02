package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void deleteMentee(long menteeId, long mentorId) {
        List<User> mentees = getUserRelations(mentorId,
                User::getMentees,
                "Mentor not found");

        deleteUserRelation(mentees, menteeId, "Mentor has no such mentee");
    }

    public void deleteMentor(long menteeId, long mentorId) {
        List<User> mentors = getUserRelations(menteeId,
                User::getMentors,
                "Mentee not found");

        deleteUserRelation(mentors, mentorId, "Mentee has no such mentor");
    }

    public List<UserDto> getMentees(long userId) {
        return userMapper.toDto(getUserRelations(userId,
                User::getMentees,
                "Mentor not found"));
    }

    public List<UserDto> getMentors(long userId) {
        return userMapper.toDto(getUserRelations(userId,
                User::getMentors,
                "Mentee not found"));
    }

    private void deleteUserRelation(List<User> users, long userIdToDelete, String errorMessage) {
        boolean exists = users.stream().anyMatch(user -> user.getId() == userIdToDelete);

        if (exists) {
            users.removeIf(user -> user.getId() == userIdToDelete);
        } else {
            throw new EntityNotFoundException(errorMessage);
        }
    }

    private List<User> getUserRelations(long userId,
                                        Function<User, List<User>> relationProvider,
                                        String errorMessage) {
        return userRepository.findById(userId)
                .map(relationProvider)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }
}