package school.faang.user_service.validation.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Component
public class EventValidation {
    public void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException("Empty or null title");
        }

        if (event.getStartDate() == null || event.getOwnerId() == null) {
            throw new DataValidationException("Required start date and owner id");
        }
    }

    public void validateEventDto(EventDto event, UserService userService) {
        validateEvent(event);
        List<Long> skillsId = userService.findOwnerById(event).getSkills().stream()
                .map(Skill::getId).toList();

        if (!event.getRelatedSkills().stream().allMatch(ev -> skillsId.contains(ev.getId()))) {
            throw new DataValidationException("User does not have required skills");
        }
    }
}
