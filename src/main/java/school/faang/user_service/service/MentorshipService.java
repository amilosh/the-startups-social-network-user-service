package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
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
        return userMapper.toDto(
                userRepository.findById(userId)
                .map(User::getMentees)
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found")));
    }

    @Transactional
    public List<UserDto> getMentors(long userId) {
        return userMapper.toDto(
                userRepository.findById(userId)
                .map(User::getMentors)
                .orElseThrow(() -> new EntityNotFoundException("Mentee not found")));
    }
}