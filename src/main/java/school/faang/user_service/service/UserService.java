package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import school.faang.user_service.domain.Person;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.parser.CsvParser;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PersonToUserMapper personToUserMapper;
    private final UserValidator userValidator;
    private final MentorshipService mentorshipService;
    private final CountryService countryService;
    private final EventService eventService;
    private final CsvParser parser;
    private final List<Filter<User, UserFilterDto>> userFilters;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PersonToUserMapper personToUserMapper,
                       UserValidator userValidator,
                       CountryService countryService,
                       @Lazy MentorshipService mentorshipService,
                       @Lazy EventService eventService,
                       List<Filter<User, UserFilterDto>> userFilters,
                       CsvParser parser) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.personToUserMapper = personToUserMapper;
        this.userValidator = userValidator;
        this.countryService = countryService;
        this.mentorshipService = mentorshipService;
        this.eventService = eventService;
        this.userFilters = userFilters;
        this.parser = parser;
    }

    public boolean checkUserExistence(long userId) {
        return userRepository.existsById(userId);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User not found by id: %s", id)));
    }

    public UserDto findUserDtoById(Long id) {
        return userMapper.toDto(findUserById(id));
    }

    @Transactional
    public UserDto deactivateProfile(long userId) {
        User user = findUserById(userId);
        stopAllUserActivities(user);
        markUserAsInactive(user);
        stopMentorship(user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public ProcessResultDto importUsersFromCsv(InputStream inputStream) throws IOException {
        List<Person> persons = parsePersons(inputStream);
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (Person person : persons) {
            if (processPerson(person, errors)) {
                successCount++;
            }
        }

        logProcessingSummary(persons.size(), successCount, errors.size());
        return new ProcessResultDto(successCount, errors);
    }

    private List<Person> parsePersons(InputStream inputStream) throws IOException {
        return parser.parseCsv(inputStream);
    }

    private boolean processPerson(Person person, List<String> errors) {
        try {
            User user = createUserFromPerson(person);
            userRepository.save(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            String errorMessage = handleDataIntegrityViolation(person, e);
            errors.add(errorMessage);
        } catch (Exception e) {
            logUnexpectedError(person, e, errors);
        }
        return false;
    }

    private void logUnexpectedError(Person person, Exception e, List<String> errors) {
        String errorMessage = String.format("Unexpected error for user '%s %s': %s",
                person.getFirstName(), person.getLastName(), e.getMessage());
        log.error(errorMessage, e);
        errors.add(errorMessage);
    }

    private void logProcessingSummary(int total, int successCount, int errorCount) {
        log.info("Processed {} users. {} succeeded, {} failed.", total, successCount, errorCount);
    }

    private String handleDataIntegrityViolation(Person person, DataIntegrityViolationException e) {
        String constraintName = extractConstraintName(e.getMessage());
        String errorMessage = String.format(
                "Failed to save user '%s %s'. User with this [%s] already exists.",
                person.getFirstName(),
                person.getLastName(),
                constraintName != null ? constraintName : "unknown constraint"
        );

        log.error("Data integrity violation while saving user '{} {}': {}",
                person.getFirstName(), person.getLastName(), e.getMessage(), e);

        return errorMessage;
    }

    private String extractConstraintName(String errorMessage) {
        Pattern pattern = Pattern.compile("constraint \\[([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private void stopAllUserActivities(User user) {
        removeGoals(user);
        eventService.cancelUserOwnedEvents(user.getId());
        removeOwnedEvents(user);
    }

    private void stopMentorship(User user) {
        if (userValidator.isUserMentor(user)) {
            user.getMentees().forEach(mentee -> {
                mentorshipService.moveGoalsToMentee(mentee.getId(), user.getId());
                mentorshipService.deleteMentor(mentee.getId(), user.getId());
            });
        }
    }

    private void markUserAsInactive(User user) {
        user.setActive(false);
    }

    private void removeGoals(User user) {
        user.getSetGoals().removeIf(goal -> goal.getUsers().isEmpty());
    }

    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        try (Stream<User> premiumUsersStream = userRepository.findPremiumUsers()) {
            Stream<User> filteredStream = applyFilters(premiumUsersStream, filterDto);

            return filteredStream
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public List<UserDto> getAllUsers(UserFilterDto filterDto) {
        try (Stream<User> usersStream = userRepository.findAll().stream()) {
            Stream<User> filteredStream = applyFilters(usersStream, filterDto);

            return filteredStream
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    private Stream<User> applyFilters(Stream<User> users, UserFilterDto filterDto) {
        Stream<Function<Stream<User>, Stream<User>>> filterFunctions = userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> userStream -> filter.apply(userStream, filterDto));

        return filterFunctions.reduce(
                users,
                (currentStream, filterFunction)
                        -> filterFunction.apply(currentStream),
                (stream1, stream2) -> stream1
        );
    }

    private void removeOwnedEvents(User user) {
        user.getOwnedEvents().removeIf(event -> event.getStatus() == EventStatus.CANCELED);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }

    private User createUserFromPerson(Person person) {
        String password = generateRandomPassword();
        User user = personToUserMapper.personToUser(person);
        user.setPassword(password);
        Country country = countryService.findOrCreateCountry(
                person.getContactInfo().getAddress().getCountry());
        user.setCountry(country);
        return user;
    }
}
