package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {

    @Mock
    private GoalRepository goalRepository;

    @Test
    public void testValidatePositive() {
        //goalDto
    }

}
