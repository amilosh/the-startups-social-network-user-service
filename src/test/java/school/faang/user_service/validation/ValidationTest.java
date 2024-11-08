package school.faang.user_service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidationTest {

    @InjectMocks
    private Validation validation;

    @Mock
    private UserRepository userRepository;

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    public void testValidateIdCorrect(long id) {
        assertThrows(DataValidationException.class,
                () -> validation.validateIdCorrect(id),
                "Incorrect id");
    }

    @Test
    public void testValidateUserData() {
        long id = Long.MAX_VALUE;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> validation.validateUserData(id),
                "User by ID is not found");
    }
}
