package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;

    public boolean isDeactivatedUser(long userId) {
        User user = getUserById(userId);
        List<Event> ownedEvents = user.getOwnedEvents();

        stopScheduledGoals(user);
        stopScheduledEvents(ownedEvents);


        user.setActive(false);
        userRepository.save(user);

        return true;
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new
                DataValidationException("User do not found by" + userId));
    }

    private void stopScheduledGoals(User user) {
        user.removeAllGoals();

        user.getGoals().stream()
                .filter(Goal::isEmptyUsers)
                .forEach(goal -> userRepository.deleteById(goal.getId()));

        userRepository.save(user);
    }

    private void stopScheduledEvents(List<Event> ownedEvents) {
        for (Event event : ownedEvents) {
            List<User> attendees = event.getAttendees();

            attendees.forEach(attendee -> {
                attendee.removeParticipatedEvent(event);
                userRepository.save(attendee);
            });
            event.setStatus(EventStatus.CANCELED);
            eventRepository.deleteById(event.getId());
        }
    }
}
