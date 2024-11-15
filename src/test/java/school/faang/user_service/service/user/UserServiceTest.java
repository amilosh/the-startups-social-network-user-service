package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private AvatarService avatarService;
    @InjectMocks
    private UserService userService;

    @Test
    void testGenerateRandomAvatar_success() {
        Long userId = 1L;
        String avatarUrl = "http://localhost/avatar/1.svg";
        User user = new User();
        user.setId(userId);
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(avatarService.generateRandomAvatar(anyString(), eq(userId + ".svg"))).thenReturn(avatarUrl);
        String result = userService.generateRandomAvatar();
        assertEquals(avatarUrl, result);
        assertNotNull(user.getUserProfilePic());
        assertEquals(avatarUrl, user.getUserProfilePic().getFileId());
        verify(userRepository).save(user);
    }

    @Test
    void testGenerateRandomAvatar_userNotFound() {
        Long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.generateRandomAvatar();
        });
        assertEquals("User not found", exception.getMessage());
        verify(avatarService, never()).generateRandomAvatar(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}