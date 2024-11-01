package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.validator.Validator;

@Component
public interface EventValidator extends Validator<EventDto> {
}
