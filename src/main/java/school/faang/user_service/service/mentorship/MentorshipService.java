package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
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

        boolean resultOfDeletion = removeFromListWithResult(mentor.getMentees(), mentee, mentee.getMentors(), mentor);
        if (resultOfDeletion) {
            log.info("Mentee with menteeId={} was successfully removed from the mentor with mentorId={}", menteeId, mentorId);
        } else {
            log.warn("Mentee with menteeId={} not found by the mentor with mentorId={}", menteeId, mentorId);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = validateId(menteeId);
        User mentor = validateId(mentorId);

        boolean resultOfDeletion = removeFromListWithResult(mentee.getMentors(), mentor, mentor.getMentees(), mentee);
        if (resultOfDeletion) {
            log.info("Mentor with mentorId={} was successfully removed from the mentee with menteeId={}", mentorId, menteeId);
        } else {
            log.warn("Mentor with mentorId={} not found by the mentee with menteeId={}", mentorId, menteeId);
        }
    }

    private User validateId(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("UserId is not found"));
    }

    private boolean removeFromListWithResult(List<User> users, User userForDelete, List<User> listOfUserForDelete, User whoHadSomethingRemoved) {
        boolean result = users.removeIf(user ->
                Objects.equals(user.getId(), userForDelete.getId())
                        && validateOfTheSecondUsersList(listOfUserForDelete, whoHadSomethingRemoved));
        if (result) {
            userRepository.save(whoHadSomethingRemoved);
            listOfUserForDelete.remove(whoHadSomethingRemoved);
            userRepository.save(userForDelete);
        }
        return result;
    }

    private boolean validateOfTheSecondUsersList(List<User> users, User userForDelete) {
        if (users.stream().noneMatch(user -> user.getId().equals(userForDelete.getId()))) {
            log.warn("An error occurred during deletion. A user with an ID={} in another list for deletion was not found. Please check the correctness of the database.", userForDelete.getId());
            throw new EntityNotFoundException("UserId is not found");
        }
        return true;
    }
}

