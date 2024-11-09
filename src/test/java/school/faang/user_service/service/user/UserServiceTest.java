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
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private User userJohn = new User("John");
    private User userJane = new User("Jane");

    private UserDto userDtoJohn = new UserDto("John");
    private UserDto userDtoJane = new UserDto("Jane");
    private  UserFilterDto filterDto = new UserFilterDto();
    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository, userMapper, List.of(userFilter));
    }

    @Test
    public void testGetPremiumUsersWithApplicableFilter() {
        List<User> users = List.of(userJohn, userJane);
        List<UserDto> userDtos = List.of(userDtoJohn, userDtoJane);
        when(userRepository.findAll()).thenReturn(users);
        when(userFilter.isApplicable(filterDto)).thenReturn(false);
        when(userMapper.toListDto(users)).thenReturn(userDtos);


        Stream<UserDto> result = userService.getPremiumUsers(filterDto);

        assertEquals(2, result.count());
        assertTrue(result.anyMatch(dto -> dto.getUsername().equals("John")));
        assertTrue(result.anyMatch(dto -> dto.getUsername().equals("Jane")));
   }

    @Test
    public void testGetPremiumUsers_WithNonApplicableFilter() {

        UserFilterDto filterDto = new UserFilterDto();
        List<User> users = List.of(userJohn, userJane);
        List<UserDto> userDtos = List.of(userDtoJohn, userDtoJane);

        when(userRepository.findAll()).thenReturn(users);
        when(userFilter.isApplicable(filterDto)).thenReturn(false);
        when(userMapper.toListDto(users)).thenReturn(userDtos);

        Stream<UserDto> result = userService.getPremiumUsers(filterDto);


        assertEquals(2, result.count());
        assertEquals(true, result.anyMatch(dto -> dto.getExperience().equals("John")));
        assertTrue(result.anyMatch(dto -> dto.getExperience().equals("Jane")));
    }


}

