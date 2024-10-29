package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    @Test
    public void testFindUserById() {
        when(userRepository.existsById(userId)).thenReturn(true);
        assertTrue(userService.existsById(userId));
    }

    @Test
    public void testNotFoundUserById() {
        when(userRepository.existsById(userId)).thenReturn(false);
        assertFalse(userService.existsById(userId));
    }
}
