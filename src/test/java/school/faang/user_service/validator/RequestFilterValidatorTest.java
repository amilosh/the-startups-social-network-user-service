package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.InvalidRequestFilterException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RequestFilterValidatorTest {

    @InjectMocks
    private RequestFilterValidator filterValidator;

    private RequestFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = null;
    }

    @Test
    void testValidateNullFilter() {
        assertThrows(InvalidRequestFilterException.class, () -> filterValidator.validateNullFilter(filter));
    }
}