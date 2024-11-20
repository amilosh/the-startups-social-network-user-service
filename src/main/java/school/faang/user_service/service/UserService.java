package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.request.UsersDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final MentorshipService mentorshipService;
    private final EventService eventService;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       UserValidator userValidator,
                       @Lazy MentorshipService mentorshipService,
                       @Lazy EventService eventService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
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

    /**
     * Returns list of premium users with optional filtering
     *
     * @param filter DTO with filtering parameters
     * @return list of premium users UserDTOs
     */
    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto filter) {
        try (Stream<User> premiumUsersStream = userRepository.findPremiumUsers()) {
            Stream<User> filteredStream = applyFilters(premiumUsersStream, filter);

            return filteredStream
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Returns list of all users with optional filtering.
     *
     * @param filter DTO with filtering parameters.
     * @return ist of all users UserDTOs
     */
    @Transactional
    public List<UserDto> getAllUsers(UserFilterDto filter) {
        List<User> users = userRepository.findAll();

        try (Stream<User> usersStream = users.stream()) {
            Stream<User> filteredStream = applyFilters(usersStream, filter);

            return filteredStream
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Applies filters from UserFilterDTO to users stream
     *
     * @param users  stream
     * @param filter DTO with filtration parameters
     * @return filtered users stream
     */
    private Stream<User> applyFilters(Stream<User> users, UserFilterDto filter) {
        List<Predicate<User>> predicates = new ArrayList<>();

        Optional.ofNullable(filter.getNamePattern())
                .filter(name -> !name.isEmpty())
                .ifPresent(namePattern -> predicates.add(
                        user -> user.getUsername() != null &&
                                user.getUsername().toLowerCase().contains(namePattern.toLowerCase())));

        Optional.ofNullable(filter.getAboutPattern())
                .filter(about -> !about.isEmpty())
                .ifPresent(aboutPattern -> predicates.add(
                        user -> user.getAboutMe() != null &&
                                user.getAboutMe().toLowerCase().contains(aboutPattern.toLowerCase())));

        Optional.ofNullable(filter.getEmailPattern())
                .filter(email -> !email.isEmpty())
                .ifPresent(emailPattern -> predicates.add(
                        user -> user.getEmail() != null &&
                                user.getEmail().toLowerCase().contains(emailPattern.toLowerCase())));

        Optional.ofNullable(filter.getContactPattern())
                .filter(contact -> !contact.isEmpty())
                .ifPresent(contactPattern -> predicates.add(
                        user -> user.getContacts() != null
                                && user.getContacts().stream()
                                .anyMatch(contact -> contact.getContact().toLowerCase().contains(contactPattern.toLowerCase()))));

        Optional.ofNullable(filter.getCountryPattern())
                .filter(country -> !country.isEmpty())
                .ifPresent(countryPattern -> predicates.add(
                        user -> user.getCountry() != null
                                && user.getCountry().getTitle() != null
                                && user.getCountry().getTitle().contains(countryPattern.toLowerCase())));

        Optional.ofNullable(filter.getCityPattern())
                .filter(city -> !city.isEmpty())
                .ifPresent(cityPattern -> predicates.add(
                        user -> user.getCity() != null && user.getCity().toLowerCase().contains(cityPattern.toLowerCase())));

        Optional.ofNullable(filter.getPhonePattern())
                .filter(phone -> !phone.isEmpty())
                .ifPresent(phonePattern -> predicates.add(
                        user -> user.getPhone() != null && user.getPhone().contains(phonePattern)));

        Optional.ofNullable(filter.getSkillPattern())
                .filter(skill -> !skill.isEmpty())
                .ifPresent(skillPattern -> predicates.add(
                        user -> user.getSkills() != null && user.getSkills().stream()
                                .anyMatch(skill -> skill.getTitle().toLowerCase().contains(skillPattern.toLowerCase()))));

        if (filter.getExperienceMin() != null) {
            predicates.add(user -> user.getExperience() >= filter.getExperienceMin());
        }

        if (filter.getExperienceMax() != null) {
            predicates.add(user -> user.getExperience() <= filter.getExperienceMax());
        }

        Predicate<User> allPredicates = user -> true;
        for (Predicate<User> predicate : predicates) {
            allPredicates = allPredicates.and(predicate);
        }

        return users.filter(allPredicates);
    }

    private void removeOwnedEvents(User user) {
        user.getOwnedEvents().removeIf(event -> event.getStatus() == EventStatus.CANCELED);
    }
}
