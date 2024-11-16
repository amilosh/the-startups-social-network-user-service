package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserFilter userFilter;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, goalRepository, eventRepository, mentorshipService, userMapper, List.of(userFilter), userValidator);
    }

    @Test
    void testDeactivateUserNotFound() {

        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deactivateUser(userId));

        verify(goalRepository, never()).findGoalsByUserId(userId);
        verify(eventRepository, never()).findAllByUserId(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeactivateUser_Success() {

        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

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
        verify(userMapper, times(1)).toListDto(users);
    }
}