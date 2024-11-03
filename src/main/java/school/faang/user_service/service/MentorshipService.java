package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.exception.DataValidationException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User user = getUser(userId);
        UserDto userDto = userMapper.toDto(user);
        if (userDto.getMenteeIds().isEmpty()) {
            return new ArrayList<>();
        }
        List<User> mentees = mentorshipRepository.findAllById(userDto.getMenteeIds());

        return mentees.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(long userId) {
        User user = getUser(userId);
        UserDto userDto = userMapper.toDto(user);
        if (userDto.getMentorIds().isEmpty()) {
            return new ArrayList<>();
        }
        List<User> mentors = mentorshipRepository.findAllById(userDto.getMentorIds());

        return mentors.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = getUser(mentorId);
        List<User> mentees = mentor.getMentees();
        mentees.removeIf(mentee -> mentee.getId() == menteeId);
        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getUser(menteeId);
        List<User> mentors = mentee.getMentors();
        mentors.removeIf(mentor -> mentor.getId() == mentorId);
        mentorshipRepository.save(mentee);
    }

    private User getUser(long userId) {
        if (userId < 0) {
            throw new DataValidationException("User's id cannot be less than zero");
        }

        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }
}
