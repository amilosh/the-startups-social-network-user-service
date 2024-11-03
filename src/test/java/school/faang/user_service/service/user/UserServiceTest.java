package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserValidator userValidator;

    @InjectMocks
    UserService userService;

    @Test
    void existsByIdTrue() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertTrue(userService.existsById(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void existsByIdFalse() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertFalse(userService.existsById(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void updateMentorsAndMenteesExistingUsers() {
        long menteeId = 1L;
        long mentorId = 2L;
        User mentee = new User();
        mentee.setId(menteeId);
        User mentor = new User();
        mentor.setId(mentorId);
        when(userValidator.userAlreadyExists(menteeId)).thenReturn(mentee);
        when(userValidator.userAlreadyExists(mentorId)).thenReturn(mentor);

        userService.updateMentorsAndMentees(menteeId, mentorId);

        verify(userRepository, times(1)).save(mentee);
        verify(userRepository, times(1)).save(mentor);
        assertTrue(mentor.getMentees().contains(mentee));
        assertTrue(mentee.getMentors().contains(mentor));
    }
}