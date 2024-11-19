package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/skills")
public class SkillController {
    private final SkillService service;

    @PostMapping
    public SkillDto create(@RequestBody SkillDto skillDto) {
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
