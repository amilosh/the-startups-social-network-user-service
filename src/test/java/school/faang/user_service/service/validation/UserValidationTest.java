package school.faang.user_service.service.validation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    private long secondUserId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = 101L;
        secondUserId = 102L;
        user = new User();
    }

    @Test
    public void isUserExistsTest() {
        initialiseUser(userId);

        userValidation.isUserExists(userId);

        verify(userRepository).findById(userId);
    }

    @Test
    public void entityNotFoundTest() {
        initialiseEmptyUser(userId);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userValidation.isUserExists(userId));
    }

    @Test
    public void isUserExistsTwoParametersTest() {
        initialiseUser(userId);
        initialiseUser(secondUserId);

        userValidation.isUserExists(userId, secondUserId);

        verify(userRepository).findById(userId);
        verify(userRepository).findById(secondUserId);
    }

    private void initialiseUser(long userId) {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    }

    private void initialiseEmptyUser(long userId) {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
    }
}