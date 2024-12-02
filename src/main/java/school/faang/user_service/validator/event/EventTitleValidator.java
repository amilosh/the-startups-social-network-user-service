package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

@Component
public class EventTitleValidator implements EventValidator {

    @Override
    public boolean isValid(EventDto eventDto) {
        return eventDto.getTitle() != null && !eventDto.getTitle().isBlank();
    }

    @Override
    public String getMessage() {
        return "Title cannot be blank or null";
    }
}
