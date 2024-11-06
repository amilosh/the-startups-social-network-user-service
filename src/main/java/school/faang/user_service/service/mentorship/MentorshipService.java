package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User user = mentorshipRepository.findById(userId).orElseThrow();

        return user.getMentees().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getMentors(long userId) {
        User user = mentorshipRepository.findById(userId).orElseThrow();

        return user.getMentors().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        deleteMentorAndMentee(mentorId, menteeId);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        deleteMentorAndMentee(mentorId, menteeId);
    }

    private void deleteMentorAndMentee(long mentorId, long menteeId) {
        User mentor = mentorshipRepository.findById(mentorId).orElseThrow();
        mentor.getMentees().removeIf(user -> user.getId() == menteeId);
        mentorshipRepository.save(mentor);

        User mentee = mentorshipRepository.findById(menteeId).orElseThrow();
        mentee.getMentors().removeIf(user -> user.getId() == mentorId);
        mentorshipRepository.save(mentee);
    }
}
