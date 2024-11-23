package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.domain.Address;
import school.faang.user_service.domain.ContactInfo;
import school.faang.user_service.domain.Person;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;


    @Mock
    private UserMapper userMapper;

    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private EventService eventService;

    @Mock
    private CountryService countryService;

    @Mock
    private UserValidator userValidator;
    @Mock
    private PersonToUserMapper personToUserMapper;

    @InjectMocks
    private UserService userService;

    private final long userId = 1L;
    private User user;
    private User user1;
    private Person person1;
    private Person person2;
    private Person person3;
    private Country country1;
    private Country country2;
    private List<Event> events;
    private UserDto dto;
    private List<Person> persons;
    private List<Person> personsDublicatEmail;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setOwnedEvents(Arrays.asList(new Event(), new Event()));
        user.setMentees(new ArrayList<>());
        user.setSetGoals(new ArrayList<>());
        events = new ArrayList<>();

        user1 = new User();

        dto = UserDto.builder()
                .id(userId)
                .build();

         person1 = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .contactInfo(ContactInfo.builder()
                        .email("email1@example.com")
                        .address(Address.builder()
                                .country("Country1")
                                .build())
                        .build())
                .build();

        person2 = Person.builder()
                .firstName("Jane")
                .lastName("Dav")
                .contactInfo(ContactInfo.builder()
                        .email("email2@example.com")
                        .address(Address.builder()
                                .country("Country2")
                                .build())
                        .build())
                .build();

        person3 = Person.builder()
                .firstName("Jane")
                .lastName("Dav")
                .contactInfo(ContactInfo.builder()
                        .email("email2@example.com")
                        .address(Address.builder()
                                .country("Country2")
                                .build())
                        .build())
                .build();

         country1 = Country.builder()
                .title("Country1")
                .build();

        country2 = Country.builder()
                .title("Country2")
                .build();

         persons = List.of(person1, person2);
        personsDublicatEmail =List.of(person2,person3);
    }

    @Test
    void checkUserExistenceWhenUserExistsShouldReturnTrue() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        assertTrue(userService.checkUserExistence(userId));

        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void checkUserExistenceWhenUserDoesNotExistShouldReturnFalse() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertFalse(userService.checkUserExistence(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void findUserWhenUserExistsShouldReturnUser() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findUserById(userId);
        assertEquals(userId, result.getId());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserWhenUserDoesNotExistShouldThrowEntityNotFoundException() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(userId));
        assertEquals(String.format("User not found by id: %s", userId), exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUserShouldCallRepositoryDeleteMethod() {
        User user = new User();
        user.setId(1L);

        userService.deleteUser(user);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void saveUserShouldCallRepositorySaveMethod() {
        User user = new User();
        user.setId(1L);

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserByIdWhenUserExistsShouldReturnUser() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
    }

    @Test
    void getUserByIdWhenUserDoesNotExistShouldReturnEmptyOptional() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test FindById")
    void testFindByIdPositive() {
        long userId = 1L;
        User user = User.builder()
                .id(1L)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    @DisplayName("Test FindById Negative")
    void testFindByIdNegative() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> userService.findUserById(userId));
        assertEquals(String.format("User not found by id: %s", userId), exception.getMessage());
    }

    @Test
    void testDeactivateProfile_UserFound_DeactivatedSuccessful() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userValidator.isUserMentor(user)).thenReturn(true);
        when(userMapper.toDto(user)).thenReturn(new UserDto());

        UserDto result = userService.deactivateProfile(userId);

        assertNotNull(result);
        assertFalse(user.isActive());
    }

    @Test
    void testDeactivateProfile_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deactivateProfile(userId));
    }

    @Test
    void testDeactivateProfile_UserIsMentor() {
        user.getMentees().add(setUpMentee());
        long menteeId = setUpMentee().getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(new UserDto());
        when(userValidator.isUserMentor(user)).thenReturn(true);

        userService.deactivateProfile(userId);

        assertFalse(user.isActive());
        verify(mentorshipService).moveGoalsToMentee(menteeId, userId);
        verify(mentorshipService).deleteMentor(menteeId, userId);
    }

    @Test
    void testFindUserDtoById_ThrowEntityNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(userId));
    }

    @Test
    void testFindUserDtoById_Successful() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        var result = userService.findUserDtoById(userId);

        assertEquals(result.getId(), dto.getId());
    }

    private User setUpMentee() {
        User mentee = new User();
        mentee.setId(2L);
        return mentee;

    }

//    @Test
//    void testProcessUsersValidInputSavesUsers() {
//        List<Person> persons = List.of(person1, person2);
//
//        when(personToUserMapper.personToUser(person1)).thenReturn(user);
//        when(personToUserMapper.personToUser(person2)).thenReturn(user1);
//        when(countryService.findOrCreateCountry("Country1")).thenReturn(country1);
//        when(countryService.findOrCreateCountry("Country2")).thenReturn(country2);
//
//       userService.processUsers(persons);
//
//        verify(personToUserMapper, times(1)).personToUser(person1);
//        verify(personToUserMapper,times(1)).personToUser(person2);
//        verify(userRepository, times(1)).save(user);
//
//        verify(userRepository,times(1)).save(user1);
//        verify(countryService,times(1)).findOrCreateCountry("Country1");
//        verify(countryService,times(1)).findOrCreateCountry("Country2");
//    }

    @Test
    void testProcessUsersDuplicateEmailsThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.processUsers(personsDublicatEmail)
        );

        assertEquals("Duplicate email found in CSV: email2@example.com", exception.getMessage());
    }

//    @Test
//    void testProcessUsersGeneratesPasswordsAndSetsCountry() {
//        List<Person> persons = List.of(person1);
//
//        when(personToUserMapper.personToUser(person1)).thenReturn(user);
//        when(countryService.findOrCreateCountry("Country1")).thenReturn(country1);
//
//        userService.processUsers(persons);
//
//        assertNotNull(user.getPassword());
//        assertEquals(country1, user.getCountry());
//        verify(userRepository).save(user);
//    }
}



