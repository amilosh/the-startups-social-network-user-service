package school.faang.user_service.validation;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

@Component
public class ValidationController {

    public void validateIdCorrect(Long id) {
        if (id == null || id <= 0) {
            throw new DataValidationException("Incorrect id");
        }
    }

    public void validateListIdsCorrect(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new DataValidationException("List ids is null");
        } else {
            ids.forEach(this::validateIdCorrect);
        }
    }
}