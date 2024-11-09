package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID" + userId + "not found"));

        goalRepository.findGoalsByUserId(userId).forEach(goal -> {
            if (goalRepository.findUsersByGoalId(goal.getId()).size() == 1) {
                goalRepository.deleteById(goal.getId());
                log.info("Goal with ID {} deleted for user with ID {} ", goal.getId(), userId);
            }
        });

        eventRepository.findAllByUserId(userId).forEach(event -> {
            eventRepository.deleteById(event.getId());
            log.info("Event with ID {} deleted for user with ID {} ", event.getId(), userId);
        });

        mentorshipService.stopMentorship(user);

        user.setActive(false);
        userRepository.save(user);
        log.info("User with ID {} was deactivated ", userId);
    }
}