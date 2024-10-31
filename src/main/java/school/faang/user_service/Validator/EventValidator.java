package school.faang.user_service.Validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.event.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final UserService userService;
    private final SkillMapper skillMapper;

    public void validateUserSkillsForEvent(User userOwner ,EventDto eventDto) {
        List<Skill> relatedSkills = eventDto.relatedSkills().stream()
                .map(skillDto -> skillMapper.toEntity(skillDto))
                .toList();

        if(!userHasSkills(userOwner, relatedSkills)){
            throw new DataValidationException("User don't have required skills for create event");
        }
    }

    private boolean userHasSkills(User userOwner, List<Skill> relatedSkills) {
        List<Skill> userOwnerSkills = userOwner.getSkills();

        return relatedSkills.stream()
                .allMatch(relatedSkill -> userOwnerSkills.stream()
                        .anyMatch(userskill -> userskill.getId() == relatedSkill.getId())
                );
    }
}
