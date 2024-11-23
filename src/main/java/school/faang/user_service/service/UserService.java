package school.faang.user_service.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.convertor.CsvSchemaFactory;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.domain.Person;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    private final CsvSchemaFactory schemaFactory;


    @Autowired
    public UserService(UserRepository userRepository,
                       CountryRepository countryRepository,
                       UserMapper userMapper,
                       PersonToUserMapper personToUserMapper,
                       UserValidator userValidator,
                       CountryService countryService,
                       CsvSchemaFactory schemaFactory,
                       @Lazy MentorshipService mentorshipService,
                       @Lazy EventService eventService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.personToUserMapper = personToUserMapper;
        this.userValidator = userValidator;
        this.countryService = countryService;
        this.schemaFactory = schemaFactory;
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

//    public void processUsers(List<Person> persons) {
//        Set<String> emailSet = new HashSet<>();
//        for (Person person : persons) {
//            if (!emailSet.add(person.getContactInfo().getEmail())) {
//                throw new IllegalArgumentException("Duplicate email found in CSV: " + person.getContactInfo().getEmail());
//            }
//            String password = generateRandomPassword();
//            User user = personToUserMapper.personToUser(person);
//            user.setPassword(password);
//            Country country = countryRepository.findByTitle(person.getContactInfo().getAddress().getCountry())
//                    .orElseGet(() -> {
//                        Country newCountry = new Country();
//                        newCountry.setTitle(person.getContactInfo().getAddress().getCountry());
//                        countryRepository.save(newCountry);
//                        return newCountry;
//                    });
//            user.setCountry(country);
//            userRepository.save(user);
//        }
//    }

    public void processUsers(List<Person> persons) throws IOException {
        validateEmails(persons)
                .stream()
                .map(this::createUserFromPerson)
                .forEach(userRepository::save);
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

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }

    private List<Person> validateEmails(List<Person> persons) {
        Set<String> emailSet = new HashSet<>();
        for (Person person : persons) {
            if (!emailSet.add(person.getContactInfo().getEmail())) {
                throw new IllegalArgumentException("Duplicate email found in CSV: "
                        + person.getContactInfo().getEmail());
            }
        }
        return persons;
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



//    public void processCsv(List<Person> persons) throws IOException {
//        CsvMapper csvMapper = new CsvMapper();
//        CsvSchema schema = schemaFactory.createPersonSchema();
//        MappingIterator<Person> iterator = csvMapper.readerFor(Person.class).with(schema).readValues(inputStream);
//        List<Person> persons = iterator.readAll();
//        log.info("List of person {} ", persons);
//    }
    }
