package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillAcquireDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validation.skill.SkillAcquireDtoValidation;
import school.faang.user_service.validation.skill.SkillDtoValidation;

import java.util.List;

@Component
@Validated
@RestController
@RequestMapping("/api/skills")  // Базовый URL для контроллера
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final SkillDtoValidation skillDtoValidation;
    private final SkillAcquireDtoValidation skillAcquireDtoValidation;

    @PostMapping("/create")
    public SkillDto create(@RequestBody SkillDto skillDto) throws Exception {
        skillDtoValidation.validate(skillDto);

        skillDto = skillService.create(skillDto);

        return skillDto;
    }

    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable @Min(1) Long userId) throws Exception {
        return skillService.getUserSkills(userId);
    }

    @PostMapping("/offeredSkills")
    public SkillCandidateDto getOfferedSkills(@RequestBody SkillCandidateDto skillCandidateDto) throws Exception {
        skillDtoValidation.validate(skillCandidateDto.getSkill());

        return skillService.getOfferedSkills(skillCandidateDto);
    }

    @PostMapping("/acquireSkillFromOffers")
    public SkillDto acquireSkillFromOffers(@RequestBody SkillAcquireDto skillAcquireDto) throws Exception {
        skillAcquireDtoValidation.validate(skillAcquireDto);

        return skillService.acquireSkillFromOffers(skillAcquireDto);
    }
}
