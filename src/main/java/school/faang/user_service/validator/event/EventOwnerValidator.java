package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

@Component
public class EventOwnerValidator implements EventValidator {

    @Override
    public boolean isValid(EventDto eventDto) {
        return eventDto.getOwnerId() != null;
    }

    @Override
    public String getMessage() {
        return "OwnerId cannot be null";
    }
}
