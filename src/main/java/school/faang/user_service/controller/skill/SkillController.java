package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService service;


    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);
        return service.create(skillDto);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return service.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return service.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return service.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skillDto) {
        if (skillDto == null || skillDto.getTitle() == null || skillDto.getTitle().isBlank()) {
            throw new DataValidationException("""
                    Validation failed. Check next:
                    -Skill not equal null
                    -Skill title not equal null or empty"""
            );
        }
    }
}
