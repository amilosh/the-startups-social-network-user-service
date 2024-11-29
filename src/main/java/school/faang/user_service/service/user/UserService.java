package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.pojo.person.PersonFlat;
import school.faang.user_service.pojo.person.PersonFromFile;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.random_password.PasswordGenerator;
import school.faang.user_service.utils.AvatarLibrary;
import school.faang.user_service.validator.user.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final CountryRepository countryRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final UserValidator userValidator;
    private final S3Service s3Service;
    private final AvatarLibrary avatarLibrary;
    private final RestTemplate restTemplate;
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
        log.info("User with ID {} has been scheduled for the deactivation", userId);
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

    public void addAvatar(long userId, MultipartFile file) {
        User user = userValidator.validateUser(userId);
        s3Service.uploadFile(file, user);
        userRepository.save(user);
    }

    public byte[] getAvatar(long userId) {
        User user = userValidator.validateUser(userId);
        UserProfilePic profile = user.getUserProfilePic();

        if (profile == null || profile.getFileId() == null) {
            return restTemplate.getForObject(avatarLibrary.getServiceUri(), byte[].class);
        }
        try {
            return s3Service.getFile(profile.getFileId()).readAllBytes();
        } catch (IOException e) {
            log.error("Failed to read all bytes from the transferred file");
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void loadingUsersViaFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            List<PersonFromFile> persons = new ArrayList<>();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            CsvMapper mapper = new CsvMapper();
            MappingIterator<PersonFlat> iterator = mapper.readerFor(PersonFlat.class).with(schema).readValues(inputStream);
            while (iterator.hasNext()) {
                PersonFlat flat = iterator.next();
                PersonFromFile personFromFile = userMapper.convertFlatToNested(flat);
                persons.add(personFromFile);
            }
            int personsCount = persons.size();
            int countOfThreads = getCountOfThreads(personsCount);
            ExecutorService executors = Executors.newFixedThreadPool(countOfThreads);
            for (int i = 0; i < personsCount; i++) {
                int finalI = i;
                executors.execute(() -> createNewUserFromPerson(persons.get(finalI)));
            }
            executors.shutdown();
        } catch (IOException error) {
            log.error("An error occurred while interacting with a file {}", file.getOriginalFilename(), error);
            throw new RuntimeException(error);
        }
    }

    private void createNewUserFromPerson(PersonFromFile personFromFile) {
        userValidator.validateUserForCreate(personFromFile);
        User user = userMapper.toUser(personFromFile);
        String password = createRandomPassword();
        user.setPassword(password);
        Country country = getCountry(personFromFile);
        user.setCountry(country);
        userRepository.save(user);
        log.info("User {} saved in database ", user.getUsername());
    }

    private int getCountOfThreads(int personsCount) {
        if (personsCount < 10) {
            return 3;
        }
        if (personsCount < 100) {
            return 10;
        } else {
            return 50;
        }
    }

    private String createRandomPassword() {
        return passwordGenerator.generatePassword(15, true,
                true, true, true);
    }

    private Country getCountry(PersonFromFile personFromFile) {
        String countryFromPerson = personFromFile.getContactInfo().getAddress().getCountry();
        Optional<Country> countryFromRepository = countryRepository.findByTitleIgnoreCase(countryFromPerson);
        if (countryFromRepository.isEmpty()) {
            Country newCountry = new Country();
            newCountry.setTitle(countryFromPerson);
            return countryRepository.save(newCountry);
        }
        return countryFromRepository.get();
    }
}