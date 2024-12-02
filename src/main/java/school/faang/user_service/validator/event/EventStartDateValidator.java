package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

@Component
public class EventStartDateValidator implements EventValidator {
    @Override
    public boolean isValid(EventDto eventDto) {
        return eventDto.getStartDate() != null;
    }

    @Override
    public String getMessage() {
        return "Start date cannot be null";
    }
}
