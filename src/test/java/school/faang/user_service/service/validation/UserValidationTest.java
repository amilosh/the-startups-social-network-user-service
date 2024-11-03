package school.faang.user_service.service.validation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserValidation userValidation;

    private long userId;
    private User user;

    @Test
    public void isUserExistsTest() {
        userId = 101L;
        user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userValidation.isUserExists(userId);

        verify(userRepository).findById(userId);
    }

    @Test
    public void entityNotFoundTest() {
        userId = 101L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userValidation.isUserExists(userId));
    }

    @Test
    public void areUsersExistTest() {
        userId = 101L;
        long secondUserId = 102L;
        user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(secondUserId)).thenReturn(Optional.of(user));

        userValidation.areUsersExist(userId, secondUserId);

        verify(userRepository).findById(userId);
        verify(userRepository).findById(secondUserId);
    }
}