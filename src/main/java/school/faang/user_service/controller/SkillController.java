package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;
import java.util.List;

@RestController
@AllArgsConstructor
@RequiredArgsConstructor
public class SkillController {

    @Autowired
    private SkillService skillService;

    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);
        SkillDto skillDtoReturned = skillService.create(skillDto);

        return skillDtoReturned;
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null ||
            skill.getTitle().isEmpty() ||
            skill.getTitle().isBlank()
            ) {
            throw new DataValidationException("the skill has not a title");
        }
    }

}
