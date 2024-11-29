package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import school.faang.user_service.domain.Address;
import school.faang.user_service.domain.ContactInfo;
import school.faang.user_service.domain.Education;
import school.faang.user_service.domain.Person;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.parser.CsvParser;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.UserValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    @Mock
    private CsvParser parser;


    @InjectMocks
    private UserService userService;

    private final long userId = 1L;
    private User user;
    private User user1;
    private User mockUser;
    private Person mockPerson;
    private Country country1;
    private List<Event> events;
    private UserDto dto;
    private String csvContent;
    private InputStream inputStream;
    private List<Person> people;


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

        country1 = Country.builder()
                .title("Country1")
                .build();

        csvContent = "firstName,lastName,yearOfBirth,group,studentID,email,phone,street,city,state,country,postalCode,faculty,yearOfStudy,major,GPA,status,admissionDate,graduationDate,degree,institution,completionYear,scholarship,employer\n"
                + "John,Doe,1998,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,New York,NY,USA,10001,Computer Science,3,Software Engineering,3.8,Active,2016-09-01,2020-05-30,High School Diploma,XYZ High School,2016,true,XYZ Technologies";
        inputStream = new ByteArrayInputStream(csvContent.getBytes());
        mockPerson = createMockPerson("John", "Doe", "john.doe@example.com");
        mockUser = createMockUser("JohnDoe", "john.doe@example.com");
        people = List.of(mockPerson);
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

    @Test
    void importUsersFromCsvSuccessfully() throws IOException {
        when(parser.parseCsv(inputStream)).thenReturn(people);
        when(personToUserMapper.personToUser(mockPerson)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        ProcessResultDto result = userService.importUsersFromCsv(inputStream);

        assertEquals(1, result.getСountSuccessfullySavedUsers());
        assertTrue(result.getErrors().isEmpty());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void importUsersFromCsvWhenSavingFails() throws Exception {
        when(parser.parseCsv(inputStream)).thenReturn(people);
        when(personToUserMapper.personToUser(mockPerson)).thenReturn(mockUser);
        when(userRepository.save(mockUser)).thenThrow(new DataIntegrityViolationException("could not execute statement; SQL [n/a]; constraint [users_phone_key] "));

        ProcessResultDto result = userService.importUsersFromCsv(inputStream);

        assertEquals(0, result.getСountSuccessfullySavedUsers());
        assertFalse(result.getErrors().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).contains("Failed to save user"));

        verify(userRepository, times(1)).save(mockUser);
    }

    private Person createMockPerson(String firstName, String lastName, String email) {
        Address address = new Address("123 Street", "New York", "NY", "Country1", "10001");
        ContactInfo contactInfo = new ContactInfo(email, "123456789", address);
        Education education = new Education("CS", 4, "SE", 3.8);

        return Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .contactInfo(contactInfo)
                .education(education)
                .employer("TechCorp")
                .build();
    }

    private User createMockUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("randomPassword");
        user.setPhone("123456789");
        return user;
    }
}
