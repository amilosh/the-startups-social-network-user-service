package school.faang.user_service.validation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class EventValidation {
    public void validateEvent(EventDto event) {
        if (event.getTitle().isBlank()) {
            throw new DataValidationException("Empty or null title");
        }

        if (event.getStartDate() == null || event.getOwnerId() == null) {
            throw new DataValidationException("Required start date and owner id");
        }
    }
}
