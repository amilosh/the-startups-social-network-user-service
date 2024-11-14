package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID" + userId + "not found"));

        goalRepository.findGoalsByUserId(userId).forEach(goal -> {
            goalRepository.removeUserFromGoal(userId, goal.getId());
            log.info("User with ID {} doesn't make anymore goal with ID {} ", userId, goal.getId());
            if (goalRepository.findUsersByGoalId(goal.getId()).isEmpty()) {
                goalRepository.deleteById(goal.getId());
                log.info("Goal with ID {} deleted from database ", goal.getId());
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

    public Stream<UserDto> getUser(UserFilterDto filterDto) {
        Stream<User> usersStream = userRepository.findAll().stream();
        for (UserFilter filter : userFilters) {
            if (filter != null && filter.isApplicable(filterDto)) {
                usersStream = filter.apply(usersStream, filterDto);
            }
        }

        return usersStream.map(userMapper::toDto);
    }
}