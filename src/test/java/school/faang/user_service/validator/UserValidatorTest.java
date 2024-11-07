package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void userExistsTest() {
        long userId = 101L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userValidator.isUserExists(userId);

        verify(userRepository).findById(userId);
    }

    @Test
    public void userNotExistTest() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userValidator.isUserExists(userId));
    }

    @Test
    public void areUsersExistTest() {
        long userId = 101L;
        long secondUserId = 102L;
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(secondUserId)).thenReturn(Optional.of(user));

        userValidator.areUsersExist(userId, secondUserId);

        verify(userRepository).findById(userId);
        verify(userRepository).findById(secondUserId);
    }
}

