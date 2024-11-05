package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MenteesDto;
import school.faang.user_service.dto.MentorsDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteesMapper;
import school.faang.user_service.mapper.MentorsMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipService {

    private final MenteesMapper menteesMapper;
    private final MentorsMapper mentorsMapper;
    private final UserRepository userRepository;

    public List<MenteesDto> getMentees(long userId) {

        Optional<User> user = getUser(userId);
        if (user.isPresent() && !user.get().getMentees().isEmpty()) {
            return menteesMapper.toDto(user.get().getMentees());
        } else {
            throw new RuntimeException(userId + " has no mentees");
        }
    }

    public List<MentorsDto> getMentors(long userId) {

        Optional<User> user = getUser(userId);
        if (user.isPresent() && !user.get().getMentors().isEmpty()) {
            return mentorsMapper.toDto(user.get().getMentors());
        } else {
            throw new RuntimeException(userId + " has no mentors");
        }
    }

    public void deleteMentee(long menteeId, long mentorId) {

        Optional<User> mentor = getUser(mentorId);
        if (mentor.isPresent() && !mentor.get().getMentees().isEmpty()) {
            mentor.get().getMentees().removeIf(user -> user.getId().equals(menteeId));
        } else {
            throw new RuntimeException(menteeId + " not found");
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {

        Optional<User> mentee = getUser(menteeId);
        if (mentee.isPresent() && !mentee.get().getMentees().isEmpty()) {
            mentee.get().getMentees().removeIf(user -> user.getId().equals(mentorId));
        } else {
            throw new RuntimeException(mentorId + " not found");
        }
    }

    private Optional<User> getUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new RuntimeException(userId + " not found");
        } else {
            return userRepository.findById(userId);
        }
    }
}
