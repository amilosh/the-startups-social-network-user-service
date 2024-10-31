package school.faang.user_service.Validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventValidator {
    private final SkillMapper skillMapper;

    public void validateUserSkillsForEvent(User userOwner, EventDto eventDto) {
        List<Skill> relatedSkills = eventDto.relatedSkills().stream()
                .map(skillDto -> skillMapper.toEntity(skillDto))
                .toList();

        log.debug("Checking if user has required skills: {}", relatedSkills);
        if (!userHasSkills(userOwner, relatedSkills)) {
            log.error("User {} doesn't have required skills to create event", userOwner.getId());
            throw new DataValidationException("User don't have required skills to create event");
        }

        log.info("User {} has all required skills to create event", userOwner.getId());
    }

    private boolean userHasSkills(User userOwner, List<Skill> relatedSkills) {
        List<Skill> userOwnerSkills = userOwner.getSkills();

        return relatedSkills.stream()
                .allMatch(relatedSkill -> userOwnerSkills.stream()
                        .anyMatch(userskill -> userskill.getId() == relatedSkill.getId())
                );
    }
}
