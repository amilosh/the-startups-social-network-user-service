package school.faang.user_service.validator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.InvalidRequestFilterException;

import static org.junit.jupiter.api.Assertions.*;

class RequestFilterValidatorTest {

    @InjectMocks
    private RequestFilterValidator filterValidator;

    private RequestFilterDto filter;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        filter = null;
    }

    @AfterEach
    void closeMocks() {
        try {
            mocks.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testValidateNullFilter() {
        assertThrows(InvalidRequestFilterException.class, () -> filterValidator.validateNullFilter(filter));
    }
}