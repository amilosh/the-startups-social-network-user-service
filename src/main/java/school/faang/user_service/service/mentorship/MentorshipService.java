package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long mentorId) {
        List<User> mentees = validateId(mentorId).getMentees();
        log.info("Mentees for userId={} have taken successfully from DB", mentorId);
        return userMapper.toListDto(mentees);
    }

    public List<UserDto> getMentors(long menteeId) {
        List<User> mentors = validateId(menteeId).getMentors();
        log.info("Mentors for userId={} have taken successfully from DB", menteeId);
        return userMapper.toListDto(mentors);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentee = validateId(menteeId);
        User mentor = validateId(mentorId);

        boolean resultOfDeletion = removeFromListWithResult(mentor.getMentees(), mentee);
        if (resultOfDeletion) {
            saveChangesOfUserInDB(mentor);
            log.info("Mentee with menteeId={} was successfully removed from the mentor with mentorId={}", menteeId, mentorId);
        } else {
            log.warn("Mentee with menteeId={} not found by the mentor with mentorId={}", menteeId, mentorId);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = validateId(menteeId);
        User mentor = validateId(mentorId);

        boolean resultOfDeletion = removeFromListWithResult(mentee.getMentors(), mentor);
        if (resultOfDeletion) {
            saveChangesOfUserInDB(mentee);
            log.info("Mentor with mentorId={} was successfully removed from the mentee with menteeId={}", mentorId, menteeId);
        } else {
            log.warn("Mentor with mentorId={} not found by the mentee with menteeId={}", mentorId, menteeId);
        }
    }

    public void stopMentorship(User mentor) {
        if (mentor == null) {
            throw new IllegalArgumentException("Mentor can't be empty");
        }

        if (mentor.getMentees() != null) {
            mentor.getMentees().forEach(mentee -> {
                if (mentee.getMentors() != null) {
                    mentee.getMentors().remove(mentor);
                }
                if (mentee.getSetGoals() != null) {
                    mentee.getSetGoals().forEach(goal -> goal.setMentor(null));
                }
                userRepository.save(mentee);
                log.info("Mentorship between mentor ID {} and mentee ID {} stopped", mentor.getId(), mentee.getId());
            });
        }
    }

    private User validateId(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserId " + userId + " is not found"));
    }

    private boolean removeFromListWithResult(List<User> users, User userForDelete) {
        return users.removeIf(user -> Objects.equals(user.getId(), userForDelete.getId()));
    }

    private void saveChangesOfUserInDB(User user) {
        userRepository.save(user);
    }
}