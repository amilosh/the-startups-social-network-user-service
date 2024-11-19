package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MenteesDto;
import school.faang.user_service.dto.MentorsDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteesMapper;
import school.faang.user_service.mapper.MentorsMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Log4j2
public class MentorshipService {

    private final MenteesMapper menteesMapper;
    private final MentorsMapper mentorsMapper;
    private final UserRepository userRepository;

    public List<MenteesDto> getMentees(long userId) {
        User user = getUser(userId);
            log.info("User_id {} sent his mentees", userId);
            return menteesMapper.toDto(user.getMentees());
    }

    public List<MentorsDto> getMentors(long userId) {
        User user = getUser(userId);
            log.info("User_id {} sent his mentors", userId);
            return mentorsMapper.toDto(user.getMentors());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = getUser(mentorId);
        if (!mentor.getMentees().isEmpty()) {
            mentor.getMentees().removeIf(user -> user.getId().equals(menteeId));
            log.info("Mentee {} for userId {} was deleted", menteeId, mentorId);
            saveUser(mentor);
        } else {
            log.info("Mentee {} for delete not found", menteeId);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getUser(menteeId);
        if (!mentee.getMentors().isEmpty()) {
            mentee.getMentors().removeIf(user -> user.getId().equals(mentorId));
            log.info("Mentor {} for userId {} was deleted", mentorId, menteeId);
            saveUser(mentee);
        } else {
            log.info("Mentor {} for delete not found", mentorId);
        }
    }

    private User getUser(long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                log.info("{} not found in DB", userId);
                throw new RuntimeException();
            } else {
                return user.get();
            }
        } catch (Exception e) {
            log.error("GetUser by userId {} has exception {}", userId, e.getMessage());
            throw new RuntimeException("GetUser exception " + e.getMessage());
        }
    }

    private void saveUser(User user) {
        try {
            userRepository.save(user);
            log.info("User_id {} has saved user in DB", user.getId());
        } catch (Exception e) {
            log.error("SaveUser exception {}", e.getMessage());
            throw new RuntimeException("SaveUser exception " + e.getMessage());
        }
    }
}
