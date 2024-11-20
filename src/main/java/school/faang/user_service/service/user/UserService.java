package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.pojo.person.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.random_password.PasswordGenerator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final Integer SIZE_OF_THREADS = 5;

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final UserValidator userValidator;
    private final PasswordGenerator passwordGenerator;

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userValidator.validateUser(userId);

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

    public UserDto getUser(long userId) {
        User user = userValidator.validateUser(userId);
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toListDto(users);
    }

    public void test(List<Person> persons) {
        ExecutorService executors = Executors.newFixedThreadPool(SIZE_OF_THREADS);

        for (int i = 0; i < persons.size(); i++) {
            int finalI = i;
            executors.submit(() -> createNewUser(persons.get(finalI)));
        }
    }

    private void createNewUser(Person person) {
        User user = userMapper.toUser(person);
        String password = createRandomPassword();
        user.setPassword(password);
        Country country = user.getCountry();
        validateCountry(country);

    }

    private String createRandomPassword() {
        return passwordGenerator.generatePassword(15, true,
                true,true,true);
    }
}