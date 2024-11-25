package school.faang.user_service.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.domain.Address;
import school.faang.user_service.domain.ContactInfo;
import school.faang.user_service.domain.Education;
import school.faang.user_service.domain.Person;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PersonToUserMapper personToUserMapper;
    private final UserValidator userValidator;
    private final MentorshipService mentorshipService;
    private final CountryService countryService;
    private final EventService eventService;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PersonToUserMapper personToUserMapper,
                       UserValidator userValidator,
                       CountryService countryService,
                       @Lazy MentorshipService mentorshipService,
                       @Lazy EventService eventService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.personToUserMapper = personToUserMapper;
        this.userValidator = userValidator;
        this.countryService = countryService;
        this.mentorshipService = mentorshipService;
        this.eventService = eventService;
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

    public ProcessResultDto processUsers(InputStream inputStream) throws IOException {
        List<Person> persons = parseCsv(inputStream);
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        for (Person person : persons) {
            ProcessResultDto result = processUser(person);
            if (result.getСountSuccessfullySavedUsers() > 0) {
                successCount++;
            } else {
                errors.addAll(result.getErrors());
            }
        }
        return new ProcessResultDto(successCount, errors);
    }

    protected ProcessResultDto processUser(Person person) {
        ProcessResultDto result = new ProcessResultDto(0, new ArrayList<>());
        User user = createUserFromPerson(person);
        try {
            userRepository.save(user);
            result = new ProcessResultDto(1, new ArrayList<>());
        } catch (DataIntegrityViolationException e) {
            String constraintName = extractConstraintName(e.getMessage());
            String standardErrorMessage = "Failed to save user: " + person.getFirstName() + " " + person.getLastName() +
                    ". User with this [" + constraintName + "] exists.";
            result = new ProcessResultDto(0, List.of(standardErrorMessage));
        }
        return result;
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

    private void removeOwnedEvents(User user) {
        user.getOwnedEvents().removeIf(event -> event.getStatus() == EventStatus.CANCELED);
    }

    private List<Person> parseCsv(InputStream inputStream) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<Map<String, String>> it = csvMapper.readerFor(Map.class)
                .with(schema).readValues(inputStream);

        List<Person> people = new ArrayList<>();

        while (it.hasNext()) {
            Map<String, String> row = it.next();
            Address address = new Address(
                    row.get("street"),
                    row.get("city"),
                    row.get("state"),
                    row.get("country"),
                    row.get("postalCode")
            );

            ContactInfo contactInfo = new ContactInfo(
                    row.get("email"),
                    row.get("phone"),
                    address
            );

            Person person = Person.builder()
                    .firstName(row.get("firstName"))
                    .lastName(row.get("lastName"))
                    .yearOfBirth(Integer.parseInt(row.get("yearOfBirth")))
                    .group(row.get("group"))
                    .studentID(row.get("studentID"))
                    .contactInfo(contactInfo)
                    .education(Education.builder()
                            .faculty(row.get("faculty"))
                            .yearOfStudy(Integer.parseInt(row.get("yearOfStudy")))
                            .major(row.get("major"))
                            .GPA(Double.parseDouble(row.get("GPA")))
                            .build())
                    .status(row.get("status"))
                    .admissionDate(LocalDate.parse(row.get("admissionDate")))
                    .graduationDate(LocalDate.parse(row.get("graduationDate")))
                    .scholarship(Boolean.parseBoolean(row.get("scholarship")))
                    .employer(row.get("employer"))
                    .build();
            people.add(person);
        }
        return people;
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
