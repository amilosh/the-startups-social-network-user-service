package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        log.info("Trying to delete mentee: {} for mentor: {}",
                menteeId, mentorId);
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found"));

        boolean isMenteeDeleted = mentor.getMentees().removeIf(user -> user.getId() == menteeId);

        if (isMenteeDeleted) {
            userRepository.save(mentor); // Save the updated mentor
            deleteMentor(menteeId, mentorId);
        }
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        log.info("Trying to delete mentor: {} for mentee: {}",
                mentorId, menteeId);
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("Mentee not found"));

        boolean isMentorDeleted = mentee.getMentors().removeIf(user -> user.getId() == mentorId);

        if (isMentorDeleted) {
            userRepository.save(mentee); // Save the updated mentee
            deleteMentee(menteeId, mentorId);
        }
    }

    @Transactional
    public List<UserDto> getMentees(long userId) {
        log.info("Trying to get list of mentees for user: {}",
                userId);
        return userMapper.toDto(
                userRepository.findById(userId)
                        .map(User::getMentees)
                        .orElseThrow(() -> new EntityNotFoundException("Mentor not found")));
    }

    @Transactional
    public List<UserDto> getMentors(long userId) {
        log.info("Trying to get list of mentors for user: {}",
                userId);
        return userMapper.toDto(
                userRepository.findById(userId)
                        .map(User::getMentors)
                        .orElseThrow(() -> new EntityNotFoundException("Mentee not found")));
    }
}