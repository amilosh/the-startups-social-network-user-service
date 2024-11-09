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
        Optional<User> user = getUser(userId);
        if (user.isPresent() && !user.get().getMentees().isEmpty()) {
            return menteesMapper.toDto(user.get().getMentees());
        } else {
            log.info("User_id {} has no mentees", userId);
            return new ArrayList<>();
        }
    }

    public List<MenteesDto> saveMentees(List<MenteesDto> menteesDtos, long mentorId) {
        List<User> menteesUpd = menteesMapper.toEntity(menteesDtos);
        Optional<User> user = getUser(mentorId);
        user.get().setMentees(menteesUpd);
        try {
            User userToSave = userRepository.save(user.get());
            log.info("Mentor_id {} has saved new mentees in DB", mentorId);
            return menteesMapper.toDto(userToSave.getMentees());
        } catch (Exception e) {
            log.error("SaveMentees exception {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<MentorsDto> getMentors(long userId) {
        Optional<User> user = getUser(userId);
        if (user.isPresent() && !user.get().getMentors().isEmpty()) {
            return mentorsMapper.toDto(user.get().getMentors());
        } else {
            log.info("User_id {} has no mentors", userId);
            return new ArrayList<>();
        }
    }

    public List<MentorsDto> saveMentors(List<MentorsDto> mentorsDtos, long menteeId) {
        List<User> mentorsUpd = mentorsMapper.toEntity(mentorsDtos);
        Optional<User> user = getUser(menteeId);
        user.get().setMentors(mentorsUpd);
        try {
            User userToSave = userRepository.save(user.get());
            log.info("Mentee_id {} has saved new mentors in DB", menteeId);
            return mentorsMapper.toDto(userToSave.getMentors());
        } catch (Exception e) {
            log.error("SaveMentors exception {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        Optional<User> mentor = getUser(mentorId);
        if (mentor.isPresent() && !mentor.get().getMentees().isEmpty()) {
            mentor.get().getMentees().removeIf(user -> user.getId().equals(menteeId));
        } else {
            log.info("Mentee {} for delete not found", menteeId);
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        Optional<User> mentee = getUser(menteeId);
        if (mentee.isPresent() && !mentee.get().getMentees().isEmpty()) {
            mentee.get().getMentees().removeIf(user -> user.getId().equals(mentorId));
        } else {
            log.info("Mentor {} for delete not found", mentorId);
        }
    }

    private Optional<User> getUser(long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                log.info("{} not found in DB", userId);
                throw new RuntimeException(userId + " not found in DB");
            } else {
                return user;
            }
        } catch (Exception e) {
            log.error("GetUser exception {}", e.getMessage());
            throw new RuntimeException("GetUser exception " + e.getMessage());
        }
    }
}
