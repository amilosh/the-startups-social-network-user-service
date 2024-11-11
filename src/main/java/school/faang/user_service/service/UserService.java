package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.DeactivatedUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalService goalService;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    public DeactivatedUserDto deactivateUser(long userId) {
        log.info("start to deactivate User by id {}", userId);
        User user = getUserById(userId);

        List<Event> ownedEvents = user.getOwnedEvents();

        stopScheduledGoals(user);
        stopScheduledEvents(ownedEvents);
        mentorshipService.stopMentorship(user);

        user.setActive(false);

        User savedUser = userRepository.save(user);

        log.info("User successfully deactivated by id {}", userId);

        return userMapper.toDeactivatedUserDto(savedUser);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new
                DataValidationException("User do not found by" + userId));
    }

    private void stopScheduledGoals(User user) {
        List<Goal> ownedGoals = user.getGoals();
        List<Goal> settingGoals = user.getSettingGoals();

        settingGoals.forEach(goal -> goal.removeExecutingUser(user));

        goalService.removeGoalsWithoutExecutingUsers(ownedGoals);
        goalService.removeGoalsWithoutExecutingUsers(settingGoals);

        user.removeAllGoals();
        log.info("all scheduled goals is stopped: \t ownedGoals {} \t settingGoals{}", user.getGoals(), user.getSettingGoals());
    }

    private void stopScheduledEvents(List<Event> ownedEvents) {
        for (Event event : ownedEvents) {
            List<User> attendees = event.getAttendees();

            attendees.forEach(attendee -> {
                attendee.removeParticipatedEvent(event);
                userRepository.save(attendee);
            });


            eventRepository.deleteById(event.getId());
        }
        log.info("all scheduled events is stopped or delete");
    }
}