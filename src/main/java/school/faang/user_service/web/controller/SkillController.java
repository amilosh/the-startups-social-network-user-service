package school.faang.user_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.web.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillServiceImpl;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillServiceImpl skillService;

    @PostMapping("/create")
    public SkillDto create(@RequestBody SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill title must not be empty");
        }
    }
}
