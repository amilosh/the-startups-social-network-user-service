package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.recommendation.ControllerRecommendationValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControllerRecommendationValidatorTest {
    @Mock
    ControllerRecommendationValidator controllerRecommendationValidator;

    @BeforeEach
    public void init() {
        controllerRecommendationValidator = new ControllerRecommendationValidator();
    }

    @Test
    public void testNullContentIsInvalid() {
        String nullString = null;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            controllerRecommendationValidator.validateContentRecommendation(nullString);
        });

        assertEquals("Recommendation content is null", dataValidationException.getMessage());
    }

    @Test
    public void testEmptyContentIsInvalid() {
        String emptyString = " ";

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> {
            controllerRecommendationValidator.validateContentRecommendation(emptyString);
        });

        assertEquals("Recommendation content is empty", dataValidationException.getMessage());
    }
}
