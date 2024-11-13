package school.faang.user_service.validator.event;

import school.faang.user_service.dto.event.EventDto;

public interface EventValidator {

    boolean isValid(EventDto dto);

    String getMessage();
}
