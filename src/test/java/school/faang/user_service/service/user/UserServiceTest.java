package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapperImpl;
//import school.faang.user_service.pojo.person.Address;
//import school.faang.user_service.pojo.person.ContactInfo;
//import school.faang.user_service.pojo.person.Education;
import school.faang.user_service.pojo.person.Person;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.service.user.random_password.PasswordGenerator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MentorshipService mentorshipService;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserFilter userFilter;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, goalRepository,countryRepository
                , eventRepository, mentorshipService, userMapper
                , List.of(userFilter), userValidator, passwordGenerator);
    }

    @Test
    void testDeactivateUser_Success() {

        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(true);

        when(userValidator.validateUser(userId)).thenReturn(user);

        userService.deactivateUser(userId);

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
        verify(eventRepository, times(1)).findAllByUserId(userId);
        verify(mentorshipService, times(1)).stopMentorship(user);
        verify(userRepository, times(1)).save(user);

        assertFalse(user.isActive(), "User should be deactivated");
    }

    @Test
    public void testGetUserWithApplicableFilter() {
        UserFilterDto filterDto = new UserFilterDto();
        User user = new User();
        UserDto userDto = new UserDto();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userFilter.isApplicable(filterDto)).thenReturn(true);

        when(userFilter.apply(any(Stream.class), eq(filterDto))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(userMapper.toDto(user)).thenReturn(userDto);

        Stream<UserDto> result = userService.getUser(filterDto);

        assertEquals(1, result.count());
        verify(userRepository, times(1)).findAll();
        verify(userFilter, times(1)).isApplicable(filterDto);
        verify(userFilter, times(1)).apply(any(Stream.class), eq(filterDto));

    }

    @Test
    public void testGetUserWithNonApplicableFilter() {

        UserFilterDto filterDto = new UserFilterDto();
        User user = new User();
        UserDto userDto = new UserDto();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userFilter.isApplicable(filterDto)).thenReturn(false);
        lenient().when(userMapper.toDto(user)).thenReturn(userDto);

        Stream<UserDto> result = userService.getUser(filterDto);

        assertEquals(1, result.count());
        verify(userRepository, times(1)).findAll();
        verify(userFilter, times(1)).isApplicable(filterDto);
        verify(userFilter, never()).apply(any(Stream.class), eq(filterDto));
    }

    @Test
    public void testGetUserSuccess() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        UserDto userDto = new UserDto();
        userDto.setId(userId);

        when(userValidator.validateUser(userId)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userId);

        assertEquals(userDto, result);
        verify(userValidator, times(1)).validateUser(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    public void testGetUsersByIdsSuccess() {
        List<Long> ids = List.of(1L, 2L);
        List<User> users = List.of(new User(), new User());
        List<UserDto> usersDtos = List.of(new UserDto(), new UserDto());

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toListDto(users)).thenReturn(usersDtos);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(usersDtos, result);
        verify(userRepository, times(1)).findAllById(ids);
        verify(userMapper, times(2)).toListDto(users);
    }

//    @Test
//    public void testLoadingUsersViaFileSuccess() {
//        Country firstCountry = Country.builder().id(1L).title("Россия").build();
//        Country secondCountry = Country.builder().id(2L).title("Казахстан").build();
//        Iterable<Country> manyCountries = List.of(firstCountry, secondCountry);
//        Education education = Education.builder().major("major").yearOfStudy(2024).faculty("faculty").build();
//        Address address = Address.builder().country("Россия").state("state").build();
//        ContactInfo contactInfo = ContactInfo.builder().address(address).build();
//        Person firstPerson = Person.builder().firstName("Рома").education(education).employer("employer")
//                .lastName("Нифонтов").contactInfo(contactInfo).build();
//        Person secondPerson = Person.builder().firstName("Юрий").education(education).employer("employer")
//                .lastName("Чусовитин").contactInfo(contactInfo).build();
//        Person thirdPerson = Person.builder().firstName("Иван").education(education).employer("employer")
//                .lastName("Бессонов").contactInfo(contactInfo).build();
//        List<Person> persons = List.of(firstPerson, secondPerson, thirdPerson);
//
//        when(countryRepository.findAll()).thenReturn(manyCountries);
//        when(passwordGenerator.generatePassword(15, true,
//                true,true,true)).thenReturn("ksdfklsklfkslklfds");
//
//        try {
//            userService.loadingUsersViaFile(persons);
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        verify(countryRepository, times(3)).findAll();
//        verify(userRepository, times(3)).save(any(User.class));
//    }
//
//    @Test
//    public void testLoadingUsersViaFileWithSaveCountry() {
//        Country firstCountry = Country.builder().id(1L).title("Россия").build();
//        Country secondCountry = Country.builder().id(2L).title("Казахстан").build();
//        Iterable<Country> manyCountries = List.of(firstCountry, secondCountry);
//        Education education = Education.builder().major("major").yearOfStudy(2024).faculty("faculty").build();
//        Address address = Address.builder().country("Британия").state("state").build();
//        ContactInfo contactInfo = ContactInfo.builder().address(address).build();
//        Person firstPerson = Person.builder().firstName("Рома").education(education).employer("employer")
//                .lastName("Нифонтов").contactInfo(contactInfo).build();
//        Person secondPerson = Person.builder().firstName("Юрий").education(education).employer("employer")
//                .lastName("Чусовитин").contactInfo(contactInfo).build();
//        Person thirdPerson = Person.builder().firstName("Иван").education(education).employer("employer")
//                .lastName("Бессонов").contactInfo(contactInfo).build();
//        List<Person> persons = List.of(firstPerson, secondPerson, thirdPerson);
//
//        when(countryRepository.findAll()).thenReturn(manyCountries);
//        when(passwordGenerator.generatePassword(15, true,
//                true,true,true)).thenReturn("ksdfklsklfkslklfds");
//
//        try {
//            userService.loadingUsersViaFile(persons);
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        verify(countryRepository, times(3)).findAll();
//        verify(countryRepository, times(3)).save(any(Country.class));
//        verify(userRepository, times(3)).save(any(User.class));
//    }
}