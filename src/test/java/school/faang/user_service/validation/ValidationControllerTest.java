package school.faang.user_service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@ExtendWith(MockitoExtension.class)
public class ValidationControllerTest {

    @InjectMocks
    private ValidationController validationController;

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    public void testValidateNumberId(long id) {
        assertThrows(DataValidationException.class,
                () -> validationController.validateIdCorrect(id),
                "Incorrect id");
    }

    @ParameterizedTest
    @NullSource
    public void testValidateNullId(Long id) {
        assertThrows(DataValidationException.class,
                () -> validationController.validateIdCorrect(id),
                "Incorrect id");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testValidateNullListIds(List<Long> ids) {
        assertThrows(DataValidationException.class,
                () -> validationController.validateListIdsCorrect(ids),
                "List ids is null");
    }

    @Test
    public void testValidateNumberListIds1() {
        List<Long> ids = List.of(1L, 0L);
        assertThrows(DataValidationException.class,
                () -> validationController.validateListIdsCorrect(ids),
                "Incorrect id");
    }

    @Test
    public void testValidateNumberListIds2() {
        List<Long> ids = List.of(1L, -1L);
        assertThrows(DataValidationException.class,
                () -> validationController.validateListIdsCorrect(ids),
                "Incorrect id");
    }

    @Test
    public void testValidateListIdsWithNullValue() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(null);
        assertThrowsExactly(DataValidationException.class,
                () -> validationController.validateListIdsCorrect(ids),
                "Incorrect id");
    }
}