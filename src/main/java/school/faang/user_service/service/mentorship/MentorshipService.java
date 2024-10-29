package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        List<UserDto> result = validateId(userId).getMentees().stream()
                .map(userMapper::toDto).toList();
        log.info("Mentees for userId={} have taken successfully from DB", userId);
        return result;
    }

    public List<UserDto> getMentors(long userId) {
        List<UserDto> result = validateId(userId).getMentors().stream()
                .map(userMapper::toDto).toList();
        log.info("Mentors for userId={} have taken successfully from DB", userId);
        return result;
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentee = validateId(menteeId);
        User mentor = validateId(mentorId);

        boolean resultOfDeletion = removeFromListWithResult(mentee.getMentors(), mentor);
        if (resultOfDeletion) {
            log.info("Mentee with menteeId={} was successfully removed from the mentor with mentorId={}", menteeId, mentorId);
        } else {
            log.info("Mentee with menteeId={} not found by the mentor with mentorId={}", menteeId, mentorId);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = validateId(menteeId);
        User mentor = validateId(mentorId);

        boolean resultOfDeletion = removeFromListWithResult(mentee.getMentors(), mentor);
        if (resultOfDeletion) {
            log.info("Mentor with mentorId={} was successfully removed from the mentee with menteeId={}", menteeId, mentorId);
        } else {
            log.info("Mentor with mentorId={} not found by the mentee with menteeId={}", menteeId, mentorId);
        }
    }

    private User validateId(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("UserId is not found"));
    }

    private boolean removeFromListWithResult(List<User> users, User userForDelete) {
        return users.removeIf(user -> Objects.equals(user.getId(), userForDelete.getId()));
    }

}
