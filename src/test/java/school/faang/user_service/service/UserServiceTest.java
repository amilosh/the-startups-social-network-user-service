package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private List<UserFilter> filters;
    @Mock
    private PremiumRepository premiumRepo;
    @Mock
    private UserMapper userMapper;

    private User user;
    private UserSubResponseDto userDto;
    private UserFilterDto userFilterDto;
    private final List<User> userList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userDto = UserSubResponseDto.builder()
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
    void testGetUserByIdWithExistingUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUserById(user.getId()));
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetPremiumUsers() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userDto = UserSubResponseDto.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userFilterDto = UserFilterDto.builder()
                .namePattern("testuser")
                .build();
        userList.add(user);
        when(premiumRepo.findPremiumUsers()).thenReturn(userList.stream());

        userService.getPremiumUsers(userFilterDto);

        verify(premiumRepo, times(1)).findPremiumUsers();
    }
}