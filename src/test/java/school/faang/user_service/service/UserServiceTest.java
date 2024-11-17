package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PremiumRepository premiumRepo;

    @InjectMocks
    private UserService userService;

    @Mock
    private List<UserFilter> filters;

    private User user;
    private UserDto userDto;
    private UserFilterDto userFilterDto;
    private List<User> userList = new ArrayList<>();


    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userFilterDto = UserFilterDto.builder()
                .namePattern("testuser")
                .build();

        userList.add(user);
    }

    @Test
    void testGetPremiumUsers() {
        when(premiumRepo.findPremiumUsers()).thenReturn(userList.stream());

        userService.getPremiumUsers(userFilterDto);

        verify(premiumRepo, times(1)).findPremiumUsers();
    }
}
