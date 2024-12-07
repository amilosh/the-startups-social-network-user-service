package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoalValidatorTest {
    private GoalValidator  goalValidator;

    @BeforeEach
    void setUp(){
        goalValidator = new GoalValidator();
    }

    @Test
    void testExceptionDoesNotThrown(){
        assertDoesNotThrow(()->goalValidator.validateId(23));
    }

    @Test
    void testExceptionThrown(){
        assertThrows(IllegalArgumentException.class,
                ()->goalValidator.validateId(-32));
    }
}