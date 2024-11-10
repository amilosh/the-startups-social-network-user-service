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
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper;
    @Mock
    private UserFilter userFilter;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository, userMapper, List.of(userFilter));
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
    public void testGetUser_WithNonApplicableFilter() {

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
}

