package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillAcquireDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;


@Validated
@RestController
@RequestMapping("/api/skills")  // Базовый URL для контроллера
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/create")
    public SkillDto create(@Valid @RequestBody SkillDto skillDto) throws Exception {
        skillDto = skillService.create(skillDto);

        return skillDto;
    }

    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable @Min(1) Long userId) throws Exception {
        return skillService.getUserSkills(userId);
    }

    @PostMapping("/offered-skills")
    public SkillCandidateDto getOfferedSkills(@Valid @RequestBody SkillCandidateDto skillCandidateDto) throws Exception {
        return skillService.getOfferedSkills(skillCandidateDto);
    }

    @PostMapping("/acquire-skill-from-offers")
    public SkillDto acquireSkillFromOffers(@Valid @RequestBody SkillAcquireDto skillAcquireDto) throws Exception {
        return skillService.acquireSkillFromOffers(skillAcquireDto);
    }
}
