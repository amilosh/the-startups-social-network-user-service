package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validation.skill.SkillDtoValidation;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/skills")  // Базовый URL для контроллера
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final SkillDtoValidation skillDtoValidation;

    @PostMapping("/create")
    public SkillDto create(@RequestBody SkillDto skillDto) {
        skillDtoValidation.validate(skillDto);

        skillDto = skillService.create(skillDto);

        return skillDto;
    }

    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable @Min(1) Long userId) {
        return skillService.getUserSkills(userId);
    }

    public SkillCandidateDto getOfferedSkills(SkillCandidateDto skillCandidateDto) {
        skillDtoValidation.validate(skillCandidateDto.getSkill());

        return skillService.getOfferedSkills(skillCandidateDto);
    }
}
