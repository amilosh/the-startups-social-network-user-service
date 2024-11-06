package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void checkIfUserExistsByIdReturnTrue() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = userService.checkUserExistence(userId);

        assertTrue(result);
    }

    @Test
    void checkIfUserExistsByIdReturnFalse() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = userService.checkUserExistence(userId);

        assertFalse(result);
    }

    @Test
    public void testFindUserWhenUserExist() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.findUser(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testFindUserWhenUserDoesNotExist() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.findUser(userId));

        assertEquals("User with ID " + userId + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testDeleteUserWhenUserExist() {
        User user = new User();

        userService.deleteUser(user);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testGetUserByIdWhenUserExist() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(user.getId());

        assertTrue(foundUser.isPresent(), "Expected user to be present");
        assertEquals(user.getId(), foundUser.get().getId());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testGetUserByIdWhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertTrue(userService.getUserById(userId).isEmpty(), "Expected user to be empty");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }
}