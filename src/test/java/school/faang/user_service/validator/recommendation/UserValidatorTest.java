package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Captor
    private ArgumentCaptor<Long> argumentCaptor;

    private final Long receiverId = 1L;

    @Test
    public void existsAuthorByIdSuccessTest() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userValidator.existsAuthorById(userId);

        verify(userRepository, times(1)).existsById(argumentCaptor.capture());
        assertEquals(userId, argumentCaptor.getValue());

    }

    @Test
    public void existsAuthorByIdFailTest() {
        Long authorId = 1L;
        when(userRepository.existsById(authorId)).thenReturn(false);

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class,
                        () -> userValidator.existsAuthorById(authorId)
                );

        assertEquals(String.format(UserValidator.AUTHOR_NOT_FOUND, authorId), dataValidationException.getMessage());

        verify(userRepository, times(1)).existsById(argumentCaptor.capture());
        assertEquals(authorId, argumentCaptor.getValue());
    }

    @Test
    public void existsReceiverByIdSuccessTest() {
        when(userRepository.existsById(receiverId)).thenReturn(true);

        userValidator.existsReceiverById(receiverId);

        verify(userRepository, times(1)).existsById(argumentCaptor.capture());
        assertEquals(receiverId, argumentCaptor.getValue());

    }

    @Test
    public void existsReceiverByIdFailTest() {
        when(userRepository.existsById(receiverId)).thenReturn(false);

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class,
                        () -> userValidator.existsReceiverById(receiverId)
                );

        assertEquals(String.format(UserValidator.RECEIVER_NOT_FOUND, receiverId), dataValidationException.getMessage());

        verify(userRepository, times(1)).existsById(argumentCaptor.capture());
        assertEquals(receiverId, argumentCaptor.getValue());
    }
}
