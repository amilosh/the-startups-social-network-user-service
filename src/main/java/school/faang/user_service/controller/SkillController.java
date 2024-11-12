package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final SkillValidator skillValidator;

    @PostMapping
    public ResponseEntity<SkillDto> create(@RequestBody SkillDto skillDto) {
        skillValidator.validateSkill(skillDto);

        SkillDto createdSkill = skillService.create(skillDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill);
    }

    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/user/{userId}/offers")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/acquire")
    public SkillDto acquireSkillFromOffers(@RequestParam long skillId, @RequestParam long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
