package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
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

    public void deactivateUser(long userId) {
        User user = getUserById(userId);
        List<Event> ownedEvents = user.getOwnedEvents();


        deleteDeactivatedEvents(ownedEvents);

    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new
                DataValidationException("User do not found by" + userId));
    }

    public void deleteDeactivatedEvents(List<Event> ownedEvents) {
        for (Event event : ownedEvents) {
            List<User> attendees = event.getAttendees();

            attendees.forEach(attendee -> {
                        attendee.removeParticipatedEvent(event);
                        userRepository.save(attendee);
                    });

            eventRepository.deleteById(event.getId());
        }
    }
}
