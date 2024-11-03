package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserValidator uservalidator;

    @Transactional
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public void updateMentorsAndMentees(Long menteeId, Long mentorId) {
        User mentee = uservalidator.userAlreadyExists(menteeId);
        User mentor = uservalidator.userAlreadyExists(mentorId);

        initializeLists(mentee);
        initializeLists(mentor);

        if (!mentee.getMentors().contains(mentor)) {
            mentee.getMentors().add(mentor);
        }
        if (!mentor.getMentees().contains(mentee)) {
            mentor.getMentees().add(mentee);
        }

        userRepository.save(mentee);
        userRepository.save(mentor);
    }

    private void initializeLists(User user) {
        if (user.getMentors() == null) {
            user.setMentors(new ArrayList<>());
        }
        if (user.getMentees() == null) {
            user.setMentees(new ArrayList<>());
        }
    }
}
