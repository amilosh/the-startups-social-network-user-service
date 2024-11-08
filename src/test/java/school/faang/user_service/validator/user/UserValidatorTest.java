package school.faang.user_service.validator.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void usersArePremiumShouldReturnEmptyStreamTest() {
        Premium premium = new Premium();
        premium.setEndDate(LocalDateTime.now().plusDays(30));

        User user = new User();
        user.setPremium(premium);
        User user2 = new User();
        user2.setPremium(premium);

        List<User> users = new ArrayList<>(List.of(user,user2));

        when(userRepository.findAll()).thenReturn(users);

        Stream<User> actualUsers = userValidator.validateNotPremiumUsers();

        assertEquals(new ArrayList<>(), actualUsers.toList());
    }

    @Test
    public void usersPremiumIsNullTest() {
        User user = new User();
        User user2 = new User();

        List<User> users = new ArrayList<>(List.of(user,user2));

        when(userRepository.findAll()).thenReturn(users);

        Stream<User> actualUsers = userValidator.validateNotPremiumUsers();

        assertEquals(users, actualUsers.toList());
    }

    @Test
    public void usersPremiumEndDateIsNullTest() {
        Premium premium = new Premium();

        User user = new User();
        user.setPremium(premium);
        User user2 = new User();
        user2.setPremium(premium);

        List<User> users = new ArrayList<>(List.of(user,user2));

        when(userRepository.findAll()).thenReturn(users);

        Stream<User> actualUsers = userValidator.validateNotPremiumUsers();

        assertEquals(users, actualUsers.toList());
    }

    @Test
    public void usersPremiumEndDateIsBeforeNowTest() {
        Premium premium = new Premium();
        premium.setEndDate(LocalDateTime.now().minusDays(1));

        User user = new User();
        user.setPremium(premium);
        User user2 = new User();
        user2.setPremium(premium);

        List<User> users = new ArrayList<>(List.of(user,user2));

        when(userRepository.findAll()).thenReturn(users);

        Stream<User> actualUsers = userValidator.validateNotPremiumUsers();

        assertEquals(users, actualUsers.toList());
    }
}

